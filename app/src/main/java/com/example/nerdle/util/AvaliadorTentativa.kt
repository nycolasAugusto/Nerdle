package com.example.nerdle.util

import com.example.nerdle.data.model.EstadoDigito

object AvaliadorTentativa {
    fun avaliar(palpite: String, resposta: String): List<EstadoDigito> =
        palpite.map { EstadoDigito.VAZIO } // TODO: lógica real
}