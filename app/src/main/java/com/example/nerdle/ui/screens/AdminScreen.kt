package com.example.nerdle.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nerdle.data.model.Equacao
import com.example.nerdle.ui.screens.jogo.TeclaButton
import com.example.nerdle.viewmodel.Admin.AdminUiState
import com.example.nerdle.viewmodel.admin.AdminViewModel
import com.example.nerdle.viewmodel.admin.AdminViewModelFactory

/**
 * Tela de Admin: liga o ViewModel à parte visual (AdminContent).
 * Não alterar a assinatura da função: a navegação depende dela.
 */
@Composable
fun AdminScreen(
    onVoltar: () -> Unit
) {
    val factory = remember { AdminViewModelFactory() }
    val viewModel: AdminViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    AdminContent(
        state = state,
        onSalvar = { id, expressao, ativa ->
            viewModel.salvar(id, expressao, ativa)
            // A validação do ViewModel é imediata: se falhou, a mensagem de erro já está no estado.
            // A última linha da lambda é o valor devolvido: true = salvou, false = deu erro
            viewModel.uiState.value.mensagemErro == null
        },
        onExcluir = { id -> viewModel.excluir(id) },
        onAlternarAtiva = { equacao -> viewModel.alternarAtiva(equacao) },
        onLimparErro = { viewModel.limparErro() },
        onVoltar = onVoltar
    )
}

/**
 * Parte só visual: recebe o estado e avisa as ações por funções.
 * Não conhece o ViewModel, por isso dá para usar no @Preview.
 */
@Composable
fun AdminContent(
    state: AdminUiState,
    onSalvar: (String?, String, Boolean) -> Boolean, // devolve true se a equação foi aceita
    onExcluir: (String) -> Unit,
    onAlternarAtiva: (Equacao) -> Unit,
    onLimparErro: () -> Unit,
    onVoltar: () -> Unit
) {
    // Dados do formulário: só a tela usa, por isso ficam aqui e não no ViewModel.
    // rememberSaveable (e não remember) para não perder o que foi digitado ao girar a tela
    var formularioAberto by rememberSaveable { mutableStateOf(false) }
    var idEditando by rememberSaveable { mutableStateOf<String?>(null) } // null = equação nova
    var expressao by rememberSaveable { mutableStateOf("") }
    var ativa by rememberSaveable { mutableStateOf(true) }

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
            Text(text = "Admin", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                idEditando = null
                expressao = ""
                ativa = true
                formularioAberto = true
            }) {
                Text("Adicionar")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (state.carregando) {
                CircularProgressIndicator()
            } else if (state.equacoes.isEmpty()) {
                Text("Nenhuma equação cadastrada")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.equacoes) { equacao ->
                        EquacaoItem(
                            equacao = equacao,
                            onEditar = {
                                idEditando = equacao.id
                                expressao = equacao.expressao
                                ativa = equacao.ativa
                                formularioAberto = true
                            },
                            onExcluir = { onExcluir(equacao.id) },
                            onAlternarAtiva = { onAlternarAtiva(equacao) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVoltar) { Text("Voltar") }
        }
    }

    // Formulário de criar/editar (o mesmo para os dois casos)
    if (formularioAberto) {
        AlertDialog(
            onDismissRequest = { formularioAberto = false },
            title = {
                if (idEditando == null) {
                    Text("Nova equação")
                } else {
                    Text("Editar equação")
                }
            },
            text = {
                // verticalScroll: com a tela deitada o formulário não cabe inteiro, então ele rola
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Visor da calculadora: mostra a conta montada pelo teclado
                    if (expressao.isEmpty()) {
                        Text("Monte a conta no teclado", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Text(expressao, style = MaterialTheme.typography.headlineMedium)
                    }
                    TecladoCalculadora(
                        onDigitar = { tecla -> expressao = expressao + tecla },
                        onApagar = { expressao = expressao.dropLast(1) } // tira o último caractere
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ativa")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(checked = ativa, onCheckedChange = { valor -> ativa = valor })
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // A validação é feita no ViewModel; o formulário só fecha se a equação foi aceita
                    val salvou = onSalvar(idEditando, expressao, ativa)
                    if (salvou) {
                        formularioAberto = false
                    }
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { formularioAberto = false }) { Text("Cancelar") }
            }
        )
    }

    // Mensagem de erro vinda do ViewModel
    val erro = state.mensagemErro
    if (erro != null) {
        AlertDialog(
            onDismissRequest = onLimparErro,
            title = { Text("Erro") },
            text = { Text(erro) },
            confirmButton = {
                TextButton(onClick = onLimparErro) { Text("OK") }
            }
        )
    }
}

@Composable
private fun EquacaoItem(
    equacao: Equacao,
    onEditar: () -> Unit,
    onExcluir: () -> Unit,
    onAlternarAtiva: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equacao.expressao, style = MaterialTheme.typography.titleMedium)
                if (equacao.ativa) {
                    Text(text = "Ativa", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(text = "Inativa", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Switch(checked = equacao.ativa, onCheckedChange = { onAlternarAtiva() })
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            TextButton(onClick = onEditar) { Text("Editar") }
            TextButton(onClick = onExcluir) { Text("Excluir") }
        }
    }
}

// Teclado de calculadora com as mesmas teclas e o mesmo botão (TeclaButton) do jogo
@Composable
private fun TecladoCalculadora(
    onDigitar: (Char) -> Unit,
    onApagar: () -> Unit
) {
    val linhas = listOf(
        listOf('1', '2', '3', '+'),
        listOf('4', '5', '6', '-'),
        listOf('7', '8', '9', '*'),
        listOf('0', '/', '=', '⌫')
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (linha in linhas) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (tecla in linha) {
                    TeclaButton(
                        caractere = tecla,
                        estado = null, // sem cores de acerto/erro: aqui não é uma partida
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (tecla == '⌫') {
                                onApagar()
                            } else {
                                onDigitar(tecla)
                            }
                        }
                    )
                }
            }
        }
    }
}

// Só para ver a tela no Android Studio com dados falsos (não roda no app)
@Preview(showBackground = true)
@Composable
private fun AdminPreview() {
    AdminContent(
        state = AdminUiState(
            equacoes = listOf(
                Equacao(id = "1", expressao = "12+34=46", ativa = true),
                Equacao(id = "2", expressao = "9*8-2=70", ativa = false)
            ),
            carregando = false
        ),
        onSalvar = { id, texto, ativa -> true },
        onExcluir = { id -> },
        onAlternarAtiva = { equacao -> },
        onLimparErro = {},
        onVoltar = {}
    )
}
