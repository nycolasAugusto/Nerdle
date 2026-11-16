package com.example.nerdle

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nerdle.data.model.Equacao
import com.example.nerdle.ui.screens.admin.AdminContent
import com.example.nerdle.viewmodel.Admin.AdminUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Editar precisa mandar o id da equação; com id null o ViewModel criaria uma equação duplicada.
    // Também confere o teclado: ⌫ apaga o último caractere e as teclas acrescentam no fim
    @Test
    fun editarPeloTecladoEnviaOIdEAConta() {
        var idRecebido: String? = null
        var expressaoRecebida = ""

        composeRule.setContent {
            AdminContent(
                state = AdminUiState(
                    equacoes = listOf(Equacao(id = "1", expressao = "12+34=46", ativa = true)),
                    carregando = false
                ),
                onSalvar = { id, expressao, ativa ->
                    idRecebido = id
                    expressaoRecebida = expressao
                    true
                },
                onExcluir = { id -> },
                onAlternarAtiva = { equacao -> },
                onLimparErro = {},
                onVoltar = {}
            )
        }

        composeRule.onNodeWithText("Editar").performClick()
        composeRule.onNodeWithText("⌫").performClick()
        composeRule.onNodeWithText("7").performClick()
        composeRule.onNodeWithText("Salvar").performClick()

        assertEquals("1", idRecebido)
        assertEquals("12+34=47", expressaoRecebida)
    }

    // Se o ViewModel recusar a equação, o formulário fica aberto com o que foi digitado
    @Test
    fun formularioContinuaAbertoQuandoSalvarFalha() {
        composeRule.setContent {
            AdminContent(
                state = AdminUiState(carregando = false),
                onSalvar = { id, expressao, ativa -> false },
                onExcluir = { id -> },
                onAlternarAtiva = { equacao -> },
                onLimparErro = {},
                onVoltar = {}
            )
        }

        composeRule.onNodeWithText("Adicionar").performClick()
        composeRule.onNodeWithText("Salvar").performClick()

        composeRule.onNodeWithText("Salvar").assertExists()
    }
}
