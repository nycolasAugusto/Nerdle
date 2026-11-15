package com.example.nerdle.viewmodel.jogo

import com.example.nerdle.data.model.EstadoDigito
import com.example.nerdle.util.RegrasJogo

enum class StatusJogo {
    AGUARDANDO_APELIDO,
    CARREGANDO,
    JOGANDO,
    VENCEU,
    PERDEU,
    SEM_EQUACOES
}

data class CelulaUi(
    val caractere: Char,
    val estado: EstadoDigito
)

data class JogoUiState(
    val status: StatusJogo = StatusJogo.AGUARDANDO_APELIDO,
    val tamanho: Int = RegrasJogo.TAMANHO_EQUACAO,
    val maxTentativas: Int = RegrasJogo.MAX_TENTATIVAS,
    val tentativas: List<List<CelulaUi>> = emptyList(),
    val palpiteAtual: String = "",
    val estadoTeclas: Map<Char, EstadoDigito> = emptyMap(),
    val mensagemErro: String? = null,
    val respostaCorreta: String? = null,
    val pontos: Int = 0,
    val tempoSegundos: Int = 0
)