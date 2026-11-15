package com.example.nerdle.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nerdle.ui.screens.AdminScreen
import com.example.nerdle.ui.screens.JogoScreen
import com.example.nerdle.ui.screens.LeaderboardScreen
import com.example.nerdle.ui.screens.LoginScreen

/**
 * Define de onde cada tela leva para onde.
 * As telas só recebem funções (onJogar, onVoltar...) e não conhecem a navegação.
 */
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
            JogoScreen(
                onVerRanking = { navController.navigate(Rotas.LEADERBOARD) },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.ADMIN) {
            AdminScreen(
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.LEADERBOARD) {
            LeaderboardScreen(
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}