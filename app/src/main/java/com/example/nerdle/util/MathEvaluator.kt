package com.example.nerdle.util

import com.example.nerdle.data.model.EstadoDigito

sealed class ResultadoValidacao {
    data object Valido : ResultadoValidacao()
    data class Invalido(val motivo: String) : ResultadoValidacao()
}

class AvaliadorEquacao {

    /**
     * Valida se a equação digitada tem sintaxe válida e se o cálculo está correto.
     */
    fun validarEquacao(equacao: String): ResultadoValidacao {
        if (equacao.isBlank()) {
            return ResultadoValidacao.Invalido("A equação não pode estar vazia.")
        }

        // Deve conter exatamente um sinal de igual
        val partes = equacao.split("=")
        if (partes.size != 2) {
            return ResultadoValidacao.Invalido("A equação deve conter exatamente um sinal de '='.")
        }

        val ladoEsquerdo = partes[0]
        val ladoDireito = partes[1]

        if (ladoEsquerdo.isEmpty() || ladoDireito.isEmpty()) {
            return ResultadoValidacao.Invalido("Faltam números antes ou depois do '='.")
        }

        // O lado direito deve conter apenas dígitos (sem sinais nem operadores)
        if (!ladoDireito.all { it.isDigit() }) {
            return ResultadoValidacao.Invalido("O resultado após '=' deve conter só dígitos.")
        }

        val resultadoEsperado = ladoDireito.toIntOrNull()
            ?: return ResultadoValidacao.Invalido("O resultado após '=' deve ser um número inteiro.")

        // Evita zeros à esquerda desnecessários (ex: "05")
        if (ladoDireito.length > 1 && ladoDireito.startsWith("0")) {
            return ResultadoValidacao.Invalido("Números não podem começar com zero.")
        }

        val resultadoCalculado = try {
            avaliarExpressao(ladoEsquerdo)
        } catch (e: IllegalArgumentException) {
            return ResultadoValidacao.Invalido(e.message ?: "Expressão matemática inválida.")
        }

        return if (resultadoCalculado == resultadoEsperado) {
            ResultadoValidacao.Valido
        } else {
            ResultadoValidacao.Invalido(
                "O cálculo não fecha ($ladoEsquerdo = $resultadoCalculado, não $resultadoEsperado)."
            )
        }
    }

    /**
     * Avalia uma expressão matemática respeitando a precedência dos operadores
     * (* e / antes de + e -).
     */
    fun avaliarExpressao(expressao: String): Int {
        val tokens = separarTokens(expressao)
        if (tokens.isEmpty()) throw IllegalArgumentException("Expressão vazia.")

        // Passo 1: resolver multiplicação (*) e divisão (/)
        val primeiraPassada = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "*" || token == "/") {
                val anterior = primeiraPassada.removeLastOrNull()?.toIntOrNull()
                    ?: throw IllegalArgumentException("Sintaxe inválida perto de '$token'.")
                val proximo = tokens.getOrNull(i + 1)?.toIntOrNull()
                    ?: throw IllegalArgumentException("Sintaxe inválida após '$token'.")

                val resultado = if (token == "*") {
                    anterior * proximo
                } else {
                    if (proximo == 0) throw IllegalArgumentException("Divisão por zero não permitida.")
                    if (anterior % proximo != 0) throw IllegalArgumentException("A divisão deve resultar em número inteiro.")
                    anterior / proximo
                }
                primeiraPassada.add(resultado.toString())
                i += 2
            } else {
                primeiraPassada.add(token)
                i++
            }
        }

        // Passo 2: resolver soma (+) e subtração (-)
        var resultadoFinal = primeiraPassada.firstOrNull()?.toIntOrNull()
            ?: throw IllegalArgumentException("Expressão deve começar com um número.")

        var j = 1
        while (j < primeiraPassada.size) {
            val operador = primeiraPassada[j]
            val proximoNumero = primeiraPassada.getOrNull(j + 1)?.toIntOrNull()
                ?: throw IllegalArgumentException("Operador '$operador' sem número seguinte.")

            resultadoFinal = when (operador) {
                "+" -> resultadoFinal + proximoNumero
                "-" -> resultadoFinal - proximoNumero
                else -> throw IllegalArgumentException("Operador desconhecido: $operador")
            }
            j += 2
        }

        return resultadoFinal
    }

    /**
     * Separa a expressão em números e operadores, validando caracteres e zeros à esquerda.
     */
    private fun separarTokens(expressao: String): List<String> {
        val tokens = mutableListOf<String>()
        val numeroAtual = StringBuilder()

        for (caractere in expressao) {
            when {
                caractere.isDigit() -> numeroAtual.append(caractere)
                caractere in listOf('+', '-', '*', '/') -> {
                    if (numeroAtual.isEmpty()) {
                        throw IllegalArgumentException("Dois operadores seguidos ou operador no início.")
                    }
                    val numeroTexto = numeroAtual.toString()
                    if (numeroTexto.length > 1 && numeroTexto.startsWith("0")) {
                        throw IllegalArgumentException("Zeros à esquerda não são permitidos ($numeroTexto).")
                    }
                    tokens.add(numeroTexto)
                    tokens.add(caractere.toString())
                    numeroAtual.clear()
                }
                else -> throw IllegalArgumentException("Caractere inválido: '$caractere'")
            }
        }

        if (numeroAtual.isEmpty()) {
            throw IllegalArgumentException("A expressão não pode terminar com um operador.")
        }

        val ultimoNumero = numeroAtual.toString()
        if (ultimoNumero.length > 1 && ultimoNumero.startsWith("0")) {
            throw IllegalArgumentException("Zeros à esquerda não são permitidos ($ultimoNumero).")
        }
        tokens.add(ultimoNumero)

        return tokens
    }

    /**
     * Compara o palpite com a equação secreta usando o algoritmo de duas passadas
     * (evita falsos amarelos quando há números ou operadores repetidos).
     * O palpite e a resposta precisam ter o mesmo tamanho.
     */
    fun avaliarPalpite(palpite: String, resposta: String): List<EstadoDigito> {
        require(palpite.length == resposta.length) {
            "O palpite e a resposta devem ter o mesmo tamanho."
        }

        val resultado = MutableList(palpite.length) { EstadoDigito.AUSENTE }
        val respostaRestante = resposta.toMutableList()

        // 1ª passada: marca os acertos exatos (verde)
        for (i in palpite.indices) {
            if (palpite[i] == resposta[i]) {
                resultado[i] = EstadoDigito.CORRETO
                respostaRestante[i] = '#' // marca como já consumido
            }
        }

        // 2ª passada: marca os presentes em outra posição (amarelo)
        for (i in palpite.indices) {
            if (resultado[i] == EstadoDigito.CORRETO) continue

            val indiceEncontrado = respostaRestante.indexOf(palpite[i])
            if (indiceEncontrado != -1) {
                resultado[i] = EstadoDigito.PRESENTE
                respostaRestante[indiceEncontrado] = '#' // consome para não duplicar
            }
        }

        return resultado
    }
}