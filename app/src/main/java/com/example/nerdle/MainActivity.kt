package com.example.nerdle

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.nerdle.data.repository.EquacaoRepository
import com.example.nerdle.navigation.AppNavigation
import com.example.nerdle.ui.theme.NerdleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            EquacaoRepository().observarTodas().collect { Log.d("TESTE", it.toString()) }
        }

        setContent {
            NerdleTheme {
                AppNavigation()
            }
        }
    }
}

