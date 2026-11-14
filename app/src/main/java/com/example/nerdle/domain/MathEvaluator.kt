package com.example.nerdle.domain

import kotlin.text.iterator

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

enum class TileState {
    CORRECT, // Verde: símbolo certo na posição certa
    PRESENT, // Amarelo: símbolo existe na equação, mas em outra posição
    ABSENT   // Cinza: símbolo não existe na equação
}

class MathEvaluator {

    /**
     * Valida se a equação digitada pelo jogador tem sintaxe válida e se o cálculo está correto.
     */
    fun validateEquation(equation: String): ValidationResult {
        if (equation.isBlank()) {
            return ValidationResult.Invalid("A equação não pode estar vazia.")
        }

        // Deve conter exatamente um sinal de igual
        val parts = equation.split("=")
        if (parts.size != 2) {
            return ValidationResult.Invalid("A equação deve conter exatamente um sinal de '='.")
        }

        val leftSide = parts[0]
        val rightSide = parts[1]

        if (leftSide.isEmpty() || rightSide.isEmpty()) {
            return ValidationResult.Invalid("Faltam números antes ou depois do '='.")
        }

        // O lado direito no Nerdle padrão deve ser apenas um número inteiro (sem operadores)
        val expectedResult = rightSide.toIntOrNull()
            ?: return ValidationResult.Invalid("O resultado após '=' deve ser um número inteiro.")

        // Evita zeros à esquerda desnecessários (ex: "05")
        if (rightSide.length > 1 && rightSide.startsWith("0")) {
            return ValidationResult.Invalid("Números não podem começar com zero.")
        }

        val calculatedResult = try {
            evaluateExpression(leftSide)
        } catch (e: IllegalArgumentException) {
            return ValidationResult.Invalid(e.message ?: "Expressão matemática inválida.")
        }

        return if (calculatedResult == expectedResult) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("O cálculo não fecha ($leftSide = $calculatedResult, não $expectedResult).")
        }
    }

    /**
     * Avalia uma expressão matemática respeitando a precedência de operadores (*, / antes de +, -).
     */
    fun evaluateExpression(expression: String): Int {
        val tokens = tokenize(expression)
        if (tokens.isEmpty()) throw IllegalArgumentException("Expressão vazia.")

        // Passo 1: Resolver multiplicação (*) e divisão (/)
        val firstPass = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "*" || token == "/") {
                val prev = firstPass.removeLastOrNull()?.toIntOrNull()
                    ?: throw IllegalArgumentException("Sintaxe inválida perto de '$token'.")
                val next = tokens.getOrNull(i + 1)?.toIntOrNull()
                    ?: throw IllegalArgumentException("Sintaxe inválida após '$token'.")

                val result = if (token == "*") {
                    prev * next
                } else {
                    if (next == 0) throw IllegalArgumentException("Divisão por zero não permitida.")
                    if (prev % next != 0) throw IllegalArgumentException("A divisão deve resultar em número inteiro.")
                    prev / next
                }
                firstPass.add(result.toString())
                i += 2
            } else {
                firstPass.add(token)
                i++
            }
        }

        // Passo 2: Resolver soma (+) e subtração (-)
        var finalResult = firstPass.firstOrNull()?.toIntOrNull()
            ?: throw IllegalArgumentException("Expressão deve começar com um número.")

        var j = 1
        while (j < firstPass.size) {
            val operator = firstPass[j]
            val nextNumber = firstPass.getOrNull(j + 1)?.toIntOrNull()
                ?: throw IllegalArgumentException("Operador '$operator' sem número seguinte.")

            finalResult = when (operator) {
                "+" -> finalResult + nextNumber
                "-" -> finalResult - nextNumber
                else -> throw IllegalArgumentException("Operador desconhecido: $operator")
            }
            j += 2
        }

        return finalResult
    }

    /**
     * Separa a string da expressão em números e operadores, validando caracteres e zeros à esquerda.
     */
    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        val currentNumber = StringBuilder()

        for (char in expression) {
            when {
                char.isDigit() -> currentNumber.append(char)
                char in listOf('+', '-', '*', '/') -> {
                    if (currentNumber.isEmpty()) {
                        throw IllegalArgumentException("Dois operadores seguidos ou operador no início.")
                    }
                    val numStr = currentNumber.toString()
                    if (numStr.length > 1 && numStr.startsWith("0")) {
                        throw IllegalArgumentException("Zeros à esquerda não são permitidos ($numStr).")
                    }
                    tokens.add(numStr)
                    tokens.add(char.toString())
                    currentNumber.clear()
                }
                else -> throw IllegalArgumentException("Caractere inválido: '$char'")
            }
        }

        if (currentNumber.isEmpty()) {
            throw IllegalArgumentException("A expressão não pode terminar com um operador.")
        }

        val lastNum = currentNumber.toString()
        if (lastNum.length > 1 && lastNum.startsWith("0")) {
            throw IllegalArgumentException("Zeros à esquerda não são permitidos ($lastNum).")
        }
        tokens.add(lastNum)

        return tokens
    }

    /**
     * Compara o palpite do jogador com a equação secreta usando algoritmo de duas passadas
     * (evita falsos amarelos quando há números/operadores repetidos).
     */
    fun evaluateGuess(guess: String, target: String): List<TileState> {
        require(guess.length == target.length) { "O palpite e o alvo devem ter o mesmo tamanho." }

        val result = MutableList(guess.length) { TileState.ABSENT }
        val targetRemaining = target.toMutableList()

        // 1ª Passada: Marca os acertos exatos (Verde)
        for (i in guess.indices) {
            if (guess[i] == target[i]) {
                result[i] = TileState.CORRECT
                targetRemaining[i] = '#' // Marca como já consumido
            }
        }

        // 2ª Passada: Marca os presentes fora de posição (Amarelo)
        for (i in guess.indices) {
            if (result[i] == TileState.CORRECT) continue

            val foundIndex = targetRemaining.indexOf(guess[i])
            if (foundIndex != -1) {
                result[i] = TileState.PRESENT
                targetRemaining[foundIndex] = '#' // Consome para não duplicar
            }
        }

        return result
    }
}