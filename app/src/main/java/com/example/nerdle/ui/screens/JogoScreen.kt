package com.example.nerdle.ui.screens.jogo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nerdle.data.local.AppDatabase
import com.example.nerdle.data.model.EstadoDigito
import com.example.nerdle.data.repository.EquacaoRepository
import com.example.nerdle.data.repository.LeaderboardRepository
import com.example.nerdle.data.repository.PartidaRepository
import com.example.nerdle.viewmodel.Jogo.CelulaUi
import com.example.nerdle.viewmodel.Jogo.JogoUiState
import com.example.nerdle.viewmodel.Jogo.JogoViewModel
import com.example.nerdle.viewmodel.Jogo.JogoViewModelFactory
import com.example.nerdle.viewmodel.Jogo.StatusJogo
import com.example.nerdle.ui.theme.NerdleTheme
import java.util.Locale

/**
 * Tela do Jogo Nerdle.
 * Não alterar a assinatura da função: a navegação depende dela.
 */
@Composable
fun JogoScreen(
    onVerRanking: () -> Unit,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val partidaRepo = remember { PartidaRepository(db.partidaDao()) }
    val equacaoRepo = remember { EquacaoRepository() }
    val leaderboardRepo = remember { LeaderboardRepository() }

    val viewModel: JogoViewModel = viewModel(
        factory = JogoViewModelFactory(equacaoRepo, partidaRepo, leaderboardRepo)
    )

    val state by viewModel.uiState.collectAsState()

    JogoContent(
        state = state,
        onIniciar = { viewModel.iniciar(it) },
        onDigitar = { viewModel.digitar(it) },
        onApagar = { viewModel.apagar() },
        onConfirmar = { viewModel.confirmar() },
        onJogarNovamente = { viewModel.jogarNovamente() },
        onVerRanking = onVerRanking,
        onVoltar = onVoltar
    )
}

@Composable
fun JogoContent(
    state: JogoUiState,
    onIniciar: (String) -> Unit,
    onDigitar: (Char) -> Unit,
    onApagar: () -> Unit,
    onConfirmar: () -> Unit,
    onJogarNovamente: () -> Unit,
    onVerRanking: () -> Unit,
    onVoltar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.status) {
            StatusJogo.AGUARDANDO_APELIDO -> {
                AguardandoApelidoContent(onIniciar = onIniciar, onVoltar = onVoltar)
            }
            StatusJogo.CARREGANDO -> {
                CarregandoContent()
            }
            StatusJogo.SEM_EQUACOES -> {
                SemEquacoesContent(mensagem = state.mensagemErro, onVoltar = onVoltar)
            }
            StatusJogo.JOGANDO -> {
                JogandoContent(
                    state = state,
                    onDigitar = onDigitar,
                    onApagar = onApagar,
                    onConfirmar = onConfirmar,
                    onVerRanking = onVerRanking,
                    onVoltar = onVoltar
                )
            }
            StatusJogo.VENCEU, StatusJogo.PERDEU -> {
                ResultadoContent(
                    state = state,
                    onJogarNovamente = onJogarNovamente,
                    onVerRanking = onVerRanking,
                    onVoltar = onVoltar
                )
            }
        }
    }
}

@Composable
fun AguardandoApelidoContent(
    onIniciar: (String) -> Unit,
    onVoltar: () -> Unit
) {
    var apelido by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nerdle",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = apelido,
            onValueChange = { apelido = it },
            label = { Text("Seu Apelido") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onIniciar(apelido) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Começar Jogo", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

@Composable
fun CarregandoContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Carregando equações...")
    }
}

@Composable
fun SemEquacoesContent(
    mensagem: String?,
    onVoltar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mensagem ?: "Nenhuma equação disponível no momento.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVoltar) {
            Text("Voltar")
        }
    }
}

@Composable
fun JogandoContent(
    state: JogoUiState,
    onDigitar: (Char) -> Unit,
    onApagar: () -> Unit,
    onConfirmar: () -> Unit,
    onVerRanking: () -> Unit,
    onVoltar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Topo: Voltar, Cronômetro, Ranking
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVoltar) {
                Text("Sair")
            }
            Text(
                text = formatarTempo(state.tempoSegundos),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onVerRanking) {
                Text("Ranking")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mensagem de Erro
        if (state.mensagemErro != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = state.mensagemErro,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tabuleiro
        Spacer(modifier = Modifier.height(8.dp))
        TabuleiroComponent(state = state)

        Spacer(modifier = Modifier.weight(1f))

        // Teclado Virtual (Estilo Calculadora)
        TecladoComponent(
            estadoTeclas = state.estadoTeclas,
            onDigitar = onDigitar,
            onApagar = onApagar,
            onConfirmar = onConfirmar
        )
    }
}

@Composable
fun TabuleiroComponent(state: JogoUiState) {
    val linhasTotal = state.maxTentativas
    val colunasTotal = state.tamanho

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 0 until linhasTotal) {
            val celulas = when {
                i < state.tentativas.size -> {
                    state.tentativas[i]
                }
                i == state.tentativas.size -> {
                    // Linha atual sendo digitada
                    val atual = state.palpiteAtual
                    val lista = mutableListOf<CelulaUi>()
                    repeat(colunasTotal) { c ->
                        val char = if (c < atual.length) atual[c] else ' '
                        lista.add(CelulaUi(char, EstadoDigito.VAZIO))
                    }
                    lista
                }
                else -> {
                    // Linhas vazias restantes
                    val lista = mutableListOf<CelulaUi>()
                    repeat(colunasTotal) {
                        lista.add(CelulaUi(' ', EstadoDigito.VAZIO))
                    }
                    lista
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (celula in celulas) {
                    Celulaview(celula = celula)
                }
            }
        }
    }
}

@Composable
fun Celulaview(celula: CelulaUi) {
    val (corFundo, corTexto) = when (celula.estado) {
        EstadoDigito.CORRETO -> Color(0xFF4CAF50) to Color.White
        EstadoDigito.PRESENTE -> Color(0xFFFFC107) to Color.Black
        EstadoDigito.AUSENTE -> Color(0xFF757575) to Color.White
        EstadoDigito.VAZIO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(corFundo)
            .border(
                width = 1.dp,
                color = if (celula.estado == EstadoDigito.VAZIO) MaterialTheme.colorScheme.outline else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = celula.caractere.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = corTexto
        )
    }
}

@Composable
fun TecladoComponent(
    estadoTeclas: Map<Char, EstadoDigito>,
    onDigitar: (Char) -> Unit,
    onApagar: () -> Unit,
    onConfirmar: () -> Unit
) {
    val linhas = listOf(
        listOf('1', '2', '3', '+'),
        listOf('4', '5', '6', '-'),
        listOf('7', '8', '9', '*'),
        listOf('0', '/', '=', '⌫')
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (linha in linhas) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (c in linha) {
                    if (c == '⌫') {
                        Button(
                            onClick = onApagar,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("⌫", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TeclaButton(
                            caractere = c,
                            estado = estadoTeclas[c],
                            modifier = Modifier.weight(1f),
                            onClick = { onDigitar(c) }
                        )
                    }
                }
            }
        }

        // Botão Enviar de largura total
        Button(
            onClick = onConfirmar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Enviar Palpite", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TeclaButton(
    caractere: Char,
    estado: EstadoDigito?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val corFundo = when (estado) {
        EstadoDigito.CORRETO -> Color(0xFF4CAF50)
        EstadoDigito.PRESENTE -> Color(0xFFFFC107)
        EstadoDigito.AUSENTE -> Color(0xFF757575)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val corTexto = when (estado) {
        EstadoDigito.CORRETO, EstadoDigito.AUSENTE -> Color.White
        EstadoDigito.PRESENTE -> Color.Black
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = corFundo, contentColor = corTexto),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = caractere.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ResultadoContent(
    state: JogoUiState,
    onJogarNovamente: () -> Unit,
    onVerRanking: () -> Unit,
    onVoltar: () -> Unit
) {
    val venceu = state.status == StatusJogo.VENCEU

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (venceu) "Você Venceu! 🎉" else "Fim de Jogo 😢",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = if (venceu) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.respostaCorreta != null) {
            Text(
                text = "A equação correta era:",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.respostaCorreta,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Pontos: ${state.pontos}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Tempo: ${formatarTempo(state.tempoSegundos)}", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onJogarNovamente,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Jogar de Novo")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onVerRanking,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Ver Ranking")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar ao Início")
        }
    }
}

fun formatarTempo(segundos: Int): String {
    val min = segundos / 60
    val seg = segundos % 60
    return String.format(Locale.getDefault(), "%02d:%02d", min, seg)
}

@Preview(showBackground = true)
@Composable
private fun JogoScreenPreview() {
    NerdleTheme {
        JogoContent(
            state = JogoUiState(status = StatusJogo.AGUARDANDO_APELIDO),
            onIniciar = {},
            onDigitar = {},
            onApagar = {},
            onConfirmar = {},
            onJogarNovamente = {},
            onVerRanking = {},
            onVoltar = {}
        )
    }
}
