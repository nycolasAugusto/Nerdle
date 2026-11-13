package com.example.nerdle.viewmodel.Jogo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JogoViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState: StateFlow<JogoUiState> = _uiState.asStateFlow()

    fun iniciar(apelido: String) {}
    fun digitar(c: Char) {}
    fun apagar() {}
    fun confirmar() {}
    fun jogarNovamente() {}


}