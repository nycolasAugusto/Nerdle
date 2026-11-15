
package com.example.nerdle.viewmodel.Jogo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nerdle.data.local.AppDatabase
import com.example.nerdle.data.repository.EquacaoRepository
import com.example.nerdle.data.repository.PartidaRepository

class JogoViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JogoViewModel::class.java)) {
            val banco = AppDatabase.getInstance(appContext)
            @Suppress("UNCHECKED_CAST")
            return JogoViewModel(
                equacaoRepository = EquacaoRepository(),
                partidaRepository = PartidaRepository(banco.partidaDao())
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
