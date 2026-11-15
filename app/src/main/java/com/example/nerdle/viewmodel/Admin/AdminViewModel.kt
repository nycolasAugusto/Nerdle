package com.example.nerdle.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nerdle.data.model.Equacao
import com.example.nerdle.data.repository.EquacaoRepository
import com.example.nerdle.util.AvaliadorEquacao
import com.example.nerdle.util.RegrasJogo
import com.example.nerdle.util.ResultadoValidacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.nerdle.viewmodel.Admin.AdminUiState

class AdminViewModel(
    private val equacaoRepository: EquacaoRepository
) : ViewModel() {

    private val avaliador = AvaliadorEquacao()

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            equacaoRepository.observarTodas()
                .catch { e ->
                    Log.e("Nerdle", "Erro ao buscar equações", e)
                    _uiState.update {
                        it.copy(carregando = false, mensagemErro = "Erro ao carregar as equações.")
                    }
                }
                .collect { lista ->
                    _uiState.update {
                        it.copy(equacoes = lista.sortedBy { eq -> eq.expressao }, carregando = false)
                    }
                }
        }
    }

    // id == null cria uma equação nova; id preenchido edita a existente
    fun salvar(id: String?, expressao: String, ativa: Boolean) {
        val limpa = expressao.trim().replace(" ", "")

        if (limpa.length != RegrasJogo.TAMANHO_EQUACAO) {
            mostrarErro("A equação deve ter exatamente ${RegrasJogo.TAMANHO_EQUACAO} caracteres.")
            return
        }

        val validacao = avaliador.validarEquacao(limpa)
        if (validacao is ResultadoValidacao.Invalido) {
            mostrarErro(validacao.motivo)
            return
        }

        val jaExiste = _uiState.value.equacoes.any { it.expressao == limpa && it.id != id }
        if (jaExiste) {
            mostrarErro("Essa equação já está cadastrada.")
            return
        }

        viewModelScope.launch {
            try {
                if (id == null) {
                    equacaoRepository.criar(Equacao(expressao = limpa, ativa = ativa))
                } else {
                    equacaoRepository.editar(Equacao(id = id, expressao = limpa, ativa = ativa))
                }
                _uiState.update { it.copy(mensagemErro = null) }
            } catch (e: Exception) {
                Log.e("Nerdle", "Erro ao salvar equação", e)
                mostrarErro("Não foi possível salvar a equação.")
            }
        }
    }

    fun excluir(id: String) {
        viewModelScope.launch {
            try {
                equacaoRepository.excluir(id)
            } catch (e: Exception) {
                Log.e("Nerdle", "Erro ao excluir equação", e)
                mostrarErro("Não foi possível excluir a equação.")
            }
        }
    }

    fun alternarAtiva(equacao: Equacao) {
        viewModelScope.launch {
            try {
                equacaoRepository.editar(equacao.copy(ativa = !equacao.ativa))
            } catch (e: Exception) {
                Log.e("Nerdle", "Erro ao alterar equação", e)
                mostrarErro("Não foi possível alterar a equação.")
            }
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(mensagemErro = null) }
    }

    private fun mostrarErro(mensagem: String) {
        _uiState.update { it.copy(mensagemErro = mensagem) }
    }
}






