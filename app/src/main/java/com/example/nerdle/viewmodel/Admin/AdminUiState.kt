package com.example.nerdle.viewmodel.Admin

import com.example.nerdle.data.model.Equacao

data class AdminUiState(
    val equacoes: List<Equacao> = emptyList(),
    val carregando: Boolean = true,
    val mensagemErro: String? = null
)