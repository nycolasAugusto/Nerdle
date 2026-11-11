package com.example.nerdle.data.remote

import com.example.nerdle.data.model.Equacao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EquacaoRemoteDataSource {

    private val colecao = FirebaseFirestore.getInstance().collection("equacoes")

    // Fica "ouvindo" a coleção: emite a lista toda vez que algo mudar no Firestore
    fun observarEquacoes(): Flow<List<Equacao>> = callbackFlow {
        val listener = colecao.addSnapshotListener { snapshot, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            val lista = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Equacao::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(lista)
        }
        awaitClose { listener.remove() }
    }

    suspend fun criar(equacao: Equacao) {
        colecao.add(equacao.paraMapa()).await()
    }

    suspend fun editar(equacao: Equacao) {
        colecao.document(equacao.id).set(equacao.paraMapa())
    }

    suspend fun excluir(id: String) {
        colecao.document(id).delete().await()
    }

    // O id é o nome do documento, então não precisa ser gravado como campo
    private fun Equacao.paraMapa() = mapOf(
        "expressao" to expressao,
        "ativa" to ativa
    )
}