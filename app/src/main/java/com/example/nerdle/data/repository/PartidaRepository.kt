package com.example.nerdle.data.repository

import com.example.nerdle.data.local.PartidaDao
import com.example.nerdle.data.local.PartidaEntity
import kotlinx.coroutines.flow.Flow

class PartidaRepository(private val dao: PartidaDao) {

    val ranking: Flow<List<PartidaEntity>> = dao.observarRanking()
    val historico: Flow<List<PartidaEntity>> = dao.observarHistorico()

    suspend fun salvar(partida: PartidaEntity) = dao.inserir(partida)
}