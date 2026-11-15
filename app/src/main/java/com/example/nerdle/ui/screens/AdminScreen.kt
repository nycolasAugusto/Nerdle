package com.example.nerdle.ui.screens.admin

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


@Composable
fun AdminScreen(
    onVoltar: () -> Unit
) {



    //FAZER TELA AQUI EDUARDO
    /**
     * TELA DE ADMIN  (versão em branco: a equipe de UI preenche)
     *
     * O QUE A TELA DEVE TER
     *  - Lista das equações (state.equacoes): expressão e se está ativa
     *  - Botão "Adicionar" que abre um formulário ou diálogo com:
     *      * campo da expressão (ex.: 12+34=46, sempre com 8 caracteres)
     *      * switch "ativa"
     *      * botões "Salvar" e "Cancelar"
     *  - Em cada item da lista: editar (abre o mesmo formulário preenchido),
     *      excluir e switch de ativar/desativar
     *  - Mensagem de erro quando mensagemErro != null (ex.: "O cálculo não fecha"),
     *      com um jeito de dispensar
     *  - Indicador de progresso enquanto carregando == true
     *  - Botão "Voltar"
     *
     * COM QUEM ELA SE COMUNICA
     *  - AdminViewModel (pacote viewmodel.admin), criado com a AdminViewModelFactory
     *  - AdminUiState: só LER, com vm.uiState.collectAsState()
     *      campos: equacoes, carregando, mensagemErro
     *  - Funções do ViewModel que a tela chama:
     *      salvar(id: String?, expressao: String, ativa: Boolean)
     *            -> id null = criar equação nova; id preenchido = editar a existente
     *      excluir(id: String)                -> botão de excluir de um item
     *      alternarAtiva(equacao: Equacao)    -> switch de um item
     *      limparErro()                       -> ao dispensar a mensagem de erro
     *  - Navegação: SOMENTE pelo parâmetro onVoltar
     *
     * REGRAS (MVVM)
     *  - A validação da equação NÃO é feita na tela: o ViewModel valida e devolve
     *    o motivo em mensagemErro. A tela só mostra.
     *  - NÃO importar nada de data/ (exceto a classe Equacao, só para tipar os itens),
     *    nem de util/, Room ou Firestore.
     *  - NÃO usar NavController aqui, e NÃO alterar a assinatura da função.
     *  - Dica: separar em AdminScreen (liga ao ViewModel) e AdminContent (só visual),
     *    para usar @Preview com dados falsos.
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
            Text(text = "Admin", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onVoltar) { Text("Voltar") }
        }
    }
}