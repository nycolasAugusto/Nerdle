package com.example.nerdle.viewmodel.LeaderBoard

import com.example.nerdle.data.local.PartidaEntity

data class LeaderboardUiState(
    val ranking: List<PartidaEntity> = emptyList(),
    val carregando: Boolean = true
)