package com.example.nerdle

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nerdle.data.model.ScoreEntry
import com.example.nerdle.ui.screens.leaderboard.LeaderboardContent
import com.example.nerdle.viewmodel.leaderboard.LeaderboardUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaderboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exibeMensagemQuandoNaoHaPartidas() {
        composeRule.setContent {
            LeaderboardContent(
                state = LeaderboardUiState(carregando = false),
                onVoltar = {}
            )
        }

        composeRule.onNodeWithText("Nenhuma partida ainda").assertExists()
    }

    @Test
    fun exibeDadosDaPontuacao() {
        composeRule.setContent {
            LeaderboardContent(
                state = LeaderboardUiState(
                    carregando = false,
                    ranking = listOf(
                        ScoreEntry(
                            nomeJogador = "Ana",
                            tentativas = 3,
                            tempoSegundos = 42
                        )
                    )
                ),
                onVoltar = {}
            )
        }

        composeRule.onNodeWithText("1º").assertExists()
        composeRule.onNodeWithText("Ana").assertExists()
        composeRule.onNodeWithText("3 tentativas").assertExists()
        composeRule.onNodeWithText("42s").assertExists()
    }
}
