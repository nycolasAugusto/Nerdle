package com.example.nerdle.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equacaoId: String,
    val expressao: String,
    val nomeJogador: String,
    val tentativas: Int,
    val venceu: Boolean,
    val tempoSegundos: Int,
    val pontos: Int,
    val dataHora: Long
)