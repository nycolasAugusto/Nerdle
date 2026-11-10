package com.example.nerdle.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    @Insert
    suspend fun inserir(partida: PartidaEntity)

    @Query("SELECT * FROM partidas WHERE venceu = 1 ORDER BY pontos DESC, tempoSegundos ASC LIMIT 20")
    fun observarRanking(): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas ORDER BY dataHora DESC")
    fun observarHistorico(): Flow<List<PartidaEntity>>
}