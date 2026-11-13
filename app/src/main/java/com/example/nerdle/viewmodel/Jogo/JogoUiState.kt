package com.example.nerdle.viewmodel.Jogo

import com.example.nerdle.data.model.EstadoDigito

enum class StatusJogo { AGUARDANDO_APELIDO, CARREGANDO, JOGANDO, VENCEU, PERDEU, SEM_EQUACOES }

data class CelulaUi(val caractere: Char, val estado: EstadoDigito)

data class JogoUiState(
    val status: StatusJogo = StatusJogo.AGUARDANDO_APELIDO,
    val tamanho: Int = 8,
    val maxTentativas: Int = 6,
    val tentativas: List<List<CelulaUi>> = emptyList(),
    val palpiteAtual: String = "",
    val estadoTeclas: Map<Char, EstadoDigito> = emptyMap(),
    val mensagemErro: String? = null,
    val respostaCorreta: String? = null,
    val pontos: Int = 0,
    val tempoSegundos: Int = 0




)