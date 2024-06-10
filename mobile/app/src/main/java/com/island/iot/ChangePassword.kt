package com.island.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ChangePasswordSection(
    navController: NavController, stateRepository: StateRepository
) {
    var oldPassword by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .padding(4.dp)
    ) {
        Text(
            text = "Insert your passwords",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        CardTextField(label = "Old Password", text = oldPassword, onChange = { oldPassword = it })
        CardTextField(label = "New Password", text = password, onChange = { password = it })
        ExtendedFloatingActionButton(
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    Icons.Filled.Check,
                    "Confirm password icon",
                    tint = colorResource(id = R.color.cream)
                )
            },
            text = { Text(text = "Change password", color = colorResource(id = R.color.cream)) },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        ExtendedFloatingActionButton(
            onClick = { Route.ACCOUNT.open(navController, stateRepository) },
            icon = {
                Icon(
                    Icons.Filled.ArrowBack,
                    "Confirm email icon",
                    tint = colorResource(id = R.color.cream)
                )
            },
            text = {
                Text(
                    text = "Return to account details",
                    color = colorResource(id = R.color.cream)
                )
            },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    val navController = rememberNavController()
    Decorations(
        navController, FAKE_REPOSITORY, Route.CHANGE_PASSWORD
    ) {
        ChangePassword(navController, FAKE_REPOSITORY)
    }
}

@Composable
fun ChangePassword(
    navController: NavController, stateRepository: StateRepository
) {
    ChangePasswordSection(navController, stateRepository)
}
