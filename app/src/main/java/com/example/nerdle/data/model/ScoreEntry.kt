package com.example.nerdle.data.model

import com.google.firebase.Timestamp

data class ScoreEntry(
    val nomeJogador: String = "",
    val tentativas: Int = 0,
    val tempoSegundos: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
)