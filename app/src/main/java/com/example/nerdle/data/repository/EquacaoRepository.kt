package com.example.nerdle.data.repository

import com.example.nerdle.data.model.Equacao
import com.example.nerdle.data.remote.EquacaoRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EquacaoRepository(
    private val remote: EquacaoRemoteDataSource = EquacaoRemoteDataSource()
) {
    // Admin vê todas
    fun observarTodas(): Flow<List<Equacao>> = remote.observarEquacoes()

    // Jogo só usa as ativas
    fun observarAtivas(): Flow<List<Equacao>> =
        remote.observarEquacoes().map { lista -> lista.filter { it.ativa } }

    suspend fun criar(equacao: Equacao) = remote.criar(equacao)
    suspend fun editar(equacao: Equacao) = remote.editar(equacao)
    suspend fun excluir(id: String) = remote.excluir(id)
}