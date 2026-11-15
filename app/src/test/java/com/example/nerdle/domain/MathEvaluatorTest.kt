package com.example.nerdle.domain

import com.example.nerdle.data.model.EstadoDigito
import com.example.nerdle.util.AvaliadorEquacao
import com.example.nerdle.util.ResultadoValidacao
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MathEvaluatorTest {

    private val avaliador = AvaliadorEquacao()

    @Test
    fun deveValidarEquacaoComPrecedenciaDeOperadoresCorreta() {
        // 2 + 3 * 4 = 14 (e não 20)
        val result = avaliador.validarEquacao("2+3*4=14")
        assertTrue(result is ResultadoValidacao.Valido)
    }

    @Test
    fun deveRejeitarEquacaoMatematicamenteIncorreta() {
        val result = avaliador.validarEquacao("10+15=30")
        assertTrue(result is ResultadoValidacao.Invalido)
    }

    @Test
    fun deveRejeitarDivisaoPorZeroOuDivisaoNaoExata() {
        assertTrue(avaliador.validarEquacao("10/0=0") is ResultadoValidacao.Invalido)
        assertTrue(avaliador.validarEquacao("10/3=3") is ResultadoValidacao.Invalido)
    }

    @Test
    fun deveAvaliarCoresCorretamenteComSimbolosRepetidos() {
        // Alvo: 12+34=46 | Palpite: 11+44=55
        val states = avaliador.avaliarPalpite("11+44=55", "12+34=46")

        assertEquals(
            listOf(
                EstadoDigito.CORRETO, // '1' na pos 0 -> Verde
                EstadoDigito.AUSENTE,  // segundo '1' não existe -> Cinza
                EstadoDigito.CORRETO, // '+' na pos 2 -> Verde
                EstadoDigito.PRESENTE,  // '4' na pos 3 existe no alvo (pos 6) -> Amarelo
                EstadoDigito.CORRETO, // '4' na pos 4 -> Verde
                EstadoDigito.CORRETO, // '=' na pos 5 -> Verde
                EstadoDigito.AUSENTE,  // '5' -> Cinza
                EstadoDigito.AUSENTE   // '5' -> Cinza
            ),
            states
        )
    }
}
