package com.example.nerdle.viewmodel.Jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nerdle.data.repository.EquacaoRepository
import com.example.nerdle.data.repository.LeaderboardRepository
import com.example.nerdle.data.repository.PartidaRepository

class JogoViewModelFactory(
    private val equacaoRepository: EquacaoRepository,
    private val partidaRepository: PartidaRepository,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JogoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JogoViewModel(
                equacaoRepository,
                partidaRepository,
                leaderboardRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}