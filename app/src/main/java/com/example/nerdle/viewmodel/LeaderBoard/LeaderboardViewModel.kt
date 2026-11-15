package com.example.nerdle.viewmodel.leaderboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nerdle.data.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            leaderboardRepository.observarRanking()
                .catch { e ->
                    Log.e("Nerdle", "Erro ao buscar ranking", e)
                    _uiState.update { it.copy(carregando = false) }
                }
                .collect { lista ->
                    _uiState.update { it.copy(ranking = lista, carregando = false) }
                }
        }
    }
}