package com.example.nerdle.viewmodel.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nerdle.data.repository.PartidaRepository
import com.example.nerdle.viewmodel.leaderboard.LeaderboardViewModel

class LeaderboardViewModelFactory(
    private val partidaRepository: PartidaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaderboardViewModel(partidaRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}