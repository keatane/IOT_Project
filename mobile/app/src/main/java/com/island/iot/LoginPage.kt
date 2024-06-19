package com.island.iot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
@Preview
fun LoginPagePreview() {
    val controller:NavController= rememberNavController()
    Decorations(controller,Route.LOGINPAGE
    ) {
        LoginPage(controller, FAKE_REPOSITORY)
    }
}

@Composable
fun LoginPage(
    navController: NavController,stateRepository: StateRepository
) {
    CredentialPage(navController,stateRepository, isRegistration = false)
}
