package com.venki.xmppdemo.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.venki.xmppdemo.ui.login.LoginScreen

@Composable
fun VeeChatNavHostGraph() {
    var navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN_SCREEN) {
        composable(Routes.LOGIN_SCREEN){
            LoginScreen(navController)
        }

        composable(Routes.JOIN_SCREEN) {
            JoinScreen(navController)
        }
    }
}