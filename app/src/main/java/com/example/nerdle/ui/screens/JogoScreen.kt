package com.example.nerdle.ui.screens.jogo

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
 * TELA EM BRANCO. A equipe de UI preenche o tabuleiro, o teclado etc.
 * Não alterar a assinatura da função: a navegação depende dela.
 */
@Composable
fun JogoScreen(
    onVerRanking: () -> Unit,
    onVoltar: () -> Unit
) {

    //CRIAR TELA AQUI - MARCELO

    /**
     * TELA DE JOGO  (versão em branco: a equipe de UI preenche)
     *
     * O QUE A TELA DEVE TER
     *  - Campo de apelido + botão "Começar"
     *  - Tabuleiro: maxTentativas linhas x tamanho colunas
     *      * linhas já enviadas: cada célula com o caractere e a cor
     *        (CORRETO = verde, PRESENTE = amarelo, AUSENTE = cinza)
     *      * linha atual: mostra palpiteAtual, sem cor
     *      * linhas restantes: vazias
     *  - Teclado: 0-9, + - * / =, botão apagar e botão enviar
     *      (opcional: colorir as teclas com estadoTeclas)
     *  - Cronômetro (tempoSegundos) e mensagem de erro quando mensagemErro != null
     *  - Fim de jogo: respostaCorreta, pontos e tempoSegundos, com os botões
     *      "Jogar de novo" e "Ver ranking"
     *  - Aviso "Nenhuma equação disponível" quando não houver equações
     *
     * COMO A TELA MUDA CONFORME state.status (StatusJogo)
     *  AGUARDANDO_APELIDO -> campo de apelido + "Começar"
     *  CARREGANDO         -> indicador de progresso
     *  JOGANDO            -> tabuleiro + teclado
     *  VENCEU / PERDEU    -> tela de resultado
     *  SEM_EQUACOES       -> aviso
     *
     * COM QUEM ELA SE COMUNICA
     *  - JogoViewModel (pacote viewmodel.jogo), criado com a JogoViewModelFactory
     *  - JogoUiState: só LER, com vm.uiState.collectAsState()
     *  - Funções do ViewModel que a tela chama:
     *      iniciar(apelido: String)   -> botão "Começar"
     *      digitar(c: Char)           -> teclas
     *      apagar()                   -> tecla apagar
     *      confirmar()                -> tecla enviar
     *      jogarNovamente()           -> botão "Jogar de novo"
     *  - Navegação: SOMENTE pelos parâmetros onVerRanking e onVoltar
     *
     * REGRAS (MVVM)
     *  - A tela só desenha o estado e avisa o ViewModel dos cliques.
     *  - NÃO importar nada de data/, util/, Room ou Firestore.
     *  - NÃO usar NavController aqui, e NÃO alterar a assinatura da função.
     *  - Dica: separar em JogoScreen (liga ao ViewModel) e JogoContent (só visual,
     *    recebe state e funções), para usar @Preview com dados falsos.
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
            Text(text = "Jogo", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onVerRanking) { Text("Ver ranking") }
            Button(onClick = onVoltar) { Text("Voltar") }
        }
    }
}