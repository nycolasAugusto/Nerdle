package com.example.nerdle.ui.screens.leaderboard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nerdle.data.model.ScoreEntry
import com.example.nerdle.data.repository.LeaderboardRepository
import com.example.nerdle.viewmodel.leaderboard.LeaderboardUiState
import com.example.nerdle.viewmodel.leaderboard.LeaderboardViewModel
import com.example.nerdle.viewmodel.leaderboard.LeaderboardViewModelFactory

@Composable
fun LeaderboardScreen(
    onVoltar: () -> Unit
) {
    val repository = remember { LeaderboardRepository() }
    val factory = remember(repository) { LeaderboardViewModelFactory(repository) }
    val viewModel: LeaderboardViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    LeaderboardContent(state = state, onVoltar = onVoltar)
}

@Composable
fun LeaderboardContent(
    state: LeaderboardUiState,
    onVoltar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Placar", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.carregando -> CircularProgressIndicator()
                state.ranking.isEmpty() -> Text("Nenhuma partida ainda")
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(state.ranking) { indice, scoreEntry ->
                            ScoreEntryItem(indice + 1, scoreEntry)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVoltar) { Text("Voltar") }
        }
    }
}

@Composable
private fun ScoreEntryItem(
    posicao: Int,
    scoreEntry: ScoreEntry
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "$posicaoº", style = MaterialTheme.typography.titleLarge)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scoreEntry.nomeJogador,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${scoreEntry.tentativas} tentativas",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${scoreEntry.tempoSegundos}s",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}