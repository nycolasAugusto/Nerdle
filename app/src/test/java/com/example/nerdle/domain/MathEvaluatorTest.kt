package com.example.nerdle.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MathEvaluatorTest {

    private val evaluator = MathEvaluator()

    @Test
    fun deveValidarEquacaoComPrecedenciaDeOperadoresCorreta() {
        // 2 + 3 * 4 = 14 (e não 20)
        val result = evaluator.validateEquation("2+3*4=14")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun deveRejeitarEquacaoMatematicamenteIncorreta() {
        val result = evaluator.validateEquation("10+15=30")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun deveRejeitarDivisaoPorZeroOuDivisaoNaoExata() {
        assertTrue(evaluator.validateEquation("10/0=0") is ValidationResult.Invalid)
        assertTrue(evaluator.validateEquation("10/3=3") is ValidationResult.Invalid)
    }

    @Test
    fun deveAvaliarCoresCorretamenteComSimbolosRepetidos() {
        // Alvo: 12+34=46 | Palpite: 11+44=55
        val states = evaluator.evaluateGuess("11+44=55", "12+34=46")

        assertEquals(
            listOf(
                TileState.CORRECT, // '1' na pos 0 -> Verde
                TileState.ABSENT,  // segundo '1' não existe -> Cinza
                TileState.CORRECT, // '+' na pos 2 -> Verde
                TileState.PRESENT,  // '4' na pos 3 existe no alvo (pos 6) -> Amarelo
                TileState.CORRECT, // '4' na pos 4 -> Verde
                TileState.CORRECT, // '=' na pos 5 -> Verde
                TileState.ABSENT,  // '5' -> Cinza
                TileState.ABSENT   // '5' -> Cinza
            ),
            states
        )
    }
}
