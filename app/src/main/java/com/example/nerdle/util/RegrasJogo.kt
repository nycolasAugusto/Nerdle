package com.example.nerdle.util

/**
 * REGRAS FIXAS DO JOGO, num só lugar.
 *
 * Quem usa:
 *  - JogoViewModel  -> tamanho do tabuleiro, nº de tentativas e teclas aceitas
 *  - AdminViewModel -> só aceita salvar equações com o tamanho certo
 *
 * Para mudar o jogo (ex.: equações de 9 caracteres), altere só aqui.
 * ATENÇÃO: as equações já cadastradas no Firestore com outro tamanho deixam de ser sorteadas.
 */
object RegrasJogo {
    const val TAMANHO_EQUACAO = 8
    const val MAX_TENTATIVAS = 6
    const val CARACTERES_VALIDOS = "0123456789+-*/="
}