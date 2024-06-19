package com.island.iot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
@Preview
fun RegisterPagePreview() {
    val navController= rememberNavController()
    Decorations(navController,Route.REGISTERPAGE
    ) {
        RegisterPage(navController, FAKE_REPOSITORY)
    }
}

@Composable
fun RegisterPage(
    navController: NavController,stateRepository: StateRepository
) {
    CredentialPage(navController,stateRepository, isRegistration = true)
}
