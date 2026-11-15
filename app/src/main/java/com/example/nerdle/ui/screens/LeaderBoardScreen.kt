package com.example.nerdle.ui.screens.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TELA EM BRANCO. A equipe de UI preenche a lista do ranking.
 * Não alterar a assinatura da função: a navegação depende dela.
 */
@Composable
fun LeaderboardScreen(
    onVoltar: () -> Unit
) {

    //FAZER TELA AQUI JOAO

    /**
     * TELA DE PLACAR  (versão em branco: a equipe de UI preenche)
     *
     * O QUE A TELA DEVE TER
     *  - Lista rolável do ranking (state.ranking), melhor jogador primeiro.
     *    Cada item mostra: posição (índice + 1), nomeJogador, pontos,
     *    tentativas e tempoSegundos
     *  - Estado vazio: "Nenhuma partida ainda" quando carregando == false
     *    e a lista estiver vazia
     *  - Indicador de progresso enquanto carregando == true
     *  - Botão "Voltar"
     *
     * COM QUEM ELA SE COMUNICA
     *  - LeaderboardViewModel (pacote viewmodel.leaderboard), criado com a
     *    LeaderboardViewModelFactory
     *  - LeaderboardUiState: só LER, com vm.uiState.collectAsState()
     *      campos: ranking, carregando
     *  - Não chama nenhuma função do ViewModel: a lista já chega ordenada e
     *    se atualiza sozinha quando uma partida termina
     *  - Navegação: SOMENTE pelo parâmetro onVoltar
     *
     * REGRAS (MVVM)
     *  - A tela NÃO ordena nem filtra a lista: só desenha o que recebe.
     *  - NÃO importar nada de data/ (exceto a classe PartidaEntity, só para tipar
     *    os itens), nem de util/, Room ou Firestore.
     *  - NÃO usar NavController aqui, e NÃO alterar a assinatura da função.
     *  - Dica: separar em LeaderboardScreen (liga ao ViewModel) e LeaderboardContent
     *    (só visual), para usar @Preview com dados falsos.
     */



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(text = "Placar", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onVoltar) { Text("Voltar") }
        }
    }
}