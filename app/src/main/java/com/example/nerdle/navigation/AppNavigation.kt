package com.example.nerdle.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nerdle.ui.screens.login.LoginScreen


@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rotas.LOGIN
    ) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                onJogar = { navController.navigate(Rotas.JOGO) },
                onAdmin = { navController.navigate(Rotas.ADMIN) }
            )
        }

        composable(Rotas.JOGO) {
            // TODO: trocar pela tela real quando existir, por exemplo:
            // JogoScreen(onVoltar = { navController.popBackStack() })
            TelaProvisoria(titulo = "Jogo", onVoltar = { navController.popBackStack() })
        }

        composable(Rotas.ADMIN) {
            // TODO: trocar pela tela real quando existir, por exemplo:
            // AdminScreen(onVoltar = { navController.popBackStack() })
            TelaProvisoria(titulo = "Admin", onVoltar = { navController.popBackStack() })
        }
    }
}

/**
 * Tela temporária só para testar o redirecionamento do login.
 * APAGAR quando as telas reais existirem.
 */
@Composable
private fun TelaProvisoria(
    titulo: String,
    onVoltar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(text = "Tela: $titulo", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onVoltar) { Text("Voltar") }
        }
    }
}