package com.example.nerdle.viewmodel.Admin

import androidx.lifecycle.ViewModel
import com.example.nerdle.data.model.Equacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun salvar(id: String?, expressao: String, ativa: Boolean) {}
    fun excluir(id: String) {}
    fun alternarAtiva(equacao: Equacao) {}
    fun limparErro() {}
}