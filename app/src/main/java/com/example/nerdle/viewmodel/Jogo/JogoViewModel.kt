package com.example.nerdle.viewmodel.jogo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nerdle.data.local.PartidaEntity
import com.example.nerdle.data.model.Equacao
import com.example.nerdle.data.model.EstadoDigito
import com.example.nerdle.data.repository.EquacaoRepository
import com.example.nerdle.data.repository.PartidaRepository
import com.example.nerdle.util.AvaliadorEquacao
import com.example.nerdle.util.RegrasJogo
import com.example.nerdle.util.ResultadoValidacao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class JogoViewModel(
    private val equacaoRepository: EquacaoRepository,
    private val partidaRepository: PartidaRepository
) : ViewModel() {

    private val avaliador = AvaliadorEquacao()

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState: StateFlow<JogoUiState> = _uiState.asStateFlow()

    private var apelido: String = ""
    private var equacaoSecreta: Equacao? = null
    private var inicioMillis: Long = 0L
    private var cronometroJob: Job? = null

    fun iniciar(apelido: String) {
        this.apelido = apelido.trim().ifBlank { "Jogador" }
        novaPartida()
    }

    fun jogarNovamente() {
        novaPartida()
    }

    fun digitar(c: Char) {
        val estado = _uiState.value
        if (estado.status != StatusJogo.JOGANDO) return
        if (c !in RegrasJogo.CARACTERES_VALIDOS) return
        if (estado.palpiteAtual.length >= estado.tamanho) return

        _uiState.update { it.copy(palpiteAtual = it.palpiteAtual + c, mensagemErro = null) }
    }

    fun apagar() {
        val estado = _uiState.value
        if (estado.status != StatusJogo.JOGANDO) return
        if (estado.palpiteAtual.isEmpty()) return

        _uiState.update { it.copy(palpiteAtual = it.palpiteAtual.dropLast(1), mensagemErro = null) }
    }

    fun confirmar() {
        val estado = _uiState.value
        val secreta = equacaoSecreta ?: return
        if (estado.status != StatusJogo.JOGANDO) return

        // O tamanho precisa ser conferido antes de avaliar, senão o avaliador lança exceção
        if (estado.palpiteAtual.length < estado.tamanho) {
            mostrarErro("Complete a equação (${estado.tamanho} caracteres).")
            return
        }

        val validacao = avaliador.validarEquacao(estado.palpiteAtual)
        if (validacao is ResultadoValidacao.Invalido) {
            mostrarErro(validacao.motivo)
            return
        }

        val estados = avaliador.avaliarPalpite(estado.palpiteAtual, secreta.expressao)
        val linha = estado.palpiteAtual.toList().zip(estados) { caractere, cor ->
            CelulaUi(caractere, cor)
        }

        val novasTentativas = estado.tentativas + listOf(linha)
        val venceu = estados.all { it == EstadoDigito.CORRETO }
        val acabou = venceu || novasTentativas.size >= estado.maxTentativas

        _uiState.update {
            it.copy(
                tentativas = novasTentativas,
                palpiteAtual = "",
                estadoTeclas = atualizarTeclas(it.estadoTeclas, linha),
                mensagemErro = null
            )
        }

        if (acabou) finalizarPartida(venceu, novasTentativas.size, secreta)
    }

    private fun novaPartida() {
        cronometroJob?.cancel()
        equacaoSecreta = null
        _uiState.value = JogoUiState(status = StatusJogo.CARREGANDO)

        viewModelScope.launch {
            try {
                val ativas = equacaoRepository.observarAtivas().first()
                    .filter { it.expressao.length == RegrasJogo.TAMANHO_EQUACAO }

                if (ativas.isEmpty()) {
                    _uiState.update { it.copy(status = StatusJogo.SEM_EQUACOES) }
                    return@launch
                }

                equacaoSecreta = ativas.random()
                iniciarCronometro()
                _uiState.update { it.copy(status = StatusJogo.JOGANDO) }
            } catch (e: Exception) {
                Log.e("Nerdle", "Erro ao buscar equações", e)
                _uiState.update {
                    it.copy(
                        status = StatusJogo.SEM_EQUACOES,
                        mensagemErro = "Não foi possível carregar as equações."
                    )
                }
            }
        }
    }

    private fun iniciarCronometro() {
        inicioMillis = System.currentTimeMillis()
        cronometroJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val segundos = ((System.currentTimeMillis() - inicioMillis) / 1000).toInt()
                _uiState.update { it.copy(tempoSegundos = segundos) }
            }
        }
    }

    private fun finalizarPartida(venceu: Boolean, tentativas: Int, secreta: Equacao) {
        cronometroJob?.cancel()

        val segundos = ((System.currentTimeMillis() - inicioMillis) / 1000).toInt()
        val pontos = calcularPontos(venceu, tentativas, segundos)

        _uiState.update {
            it.copy(
                status = if (venceu) StatusJogo.VENCEU else StatusJogo.PERDEU,
                respostaCorreta = secreta.expressao,
                pontos = pontos,
                tempoSegundos = segundos
            )
        }

        viewModelScope.launch {
            try {
                partidaRepository.salvar(
                    PartidaEntity(
                        equacaoId = secreta.id,
                        expressao = secreta.expressao,
                        nomeJogador = apelido,
                        tentativas = tentativas,
                        venceu = venceu,
                        tempoSegundos = segundos,
                        pontos = pontos,
                        dataHora = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e("Nerdle", "Erro ao salvar partida", e)
            }
        }
    }

    private fun calcularPontos(venceu: Boolean, tentativas: Int, segundos: Int): Int {
        if (!venceu) return 0
        val porTentativas = (RegrasJogo.MAX_TENTATIVAS - tentativas + 1) * 100
        val bonusTempo = (120 - segundos).coerceAtLeast(0)
        return porTentativas + bonusTempo
    }

    private fun atualizarTeclas(
        atuais: Map<Char, EstadoDigito>,
        linha: List<CelulaUi>
    ): Map<Char, EstadoDigito> {
        val novas = atuais.toMutableMap()
        for (celula in linha) {
            val anterior = novas[celula.caractere]
            if (anterior == null || prioridade(celula.estado) > prioridade(anterior)) {
                novas[celula.caractere] = celula.estado
            }
        }
        return novas
    }

    private fun prioridade(estado: EstadoDigito): Int = when (estado) {
        EstadoDigito.CORRETO -> 3
        EstadoDigito.PRESENTE -> 2
        EstadoDigito.AUSENTE -> 1
        else -> 0
    }

    private fun mostrarErro(mensagem: String) {
        _uiState.update { it.copy(mensagemErro = mensagem) }
    }
}