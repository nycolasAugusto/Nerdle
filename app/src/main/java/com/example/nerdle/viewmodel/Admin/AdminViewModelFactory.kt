package com.example.nerdle.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nerdle.data.repository.EquacaoRepository

class AdminViewModelFactory(
    private val equacaoRepository: EquacaoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(equacaoRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}