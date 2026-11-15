package com.example.nerdle.data.repository

import com.example.nerdle.data.model.ScoreEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LeaderboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val placar = firestore.collection("leaderboard")

    suspend fun salvarPontuacao(scoreEntry: ScoreEntry): String =
        placar.add(scoreEntry).await().id

    fun observarRanking(): Flow<List<ScoreEntry>> = callbackFlow {
        val listener = placar
            .orderBy("tentativas")
            .orderBy("tempoSegundos")
            .limit(20)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }

                val ranking = snapshot?.documents?.mapNotNull { documento ->
                    documento.toObject(ScoreEntry::class.java)
                } ?: emptyList()

                trySend(ranking)
            }

        awaitClose { listener.remove() }
    }
}