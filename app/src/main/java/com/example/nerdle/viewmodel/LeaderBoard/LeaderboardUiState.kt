package com.example.nerdle.viewmodel.leaderboard

import com.example.nerdle.data.model.ScoreEntry

data class LeaderboardUiState(
    val ranking: List<ScoreEntry> = emptyList(),
    val carregando: Boolean = true
)