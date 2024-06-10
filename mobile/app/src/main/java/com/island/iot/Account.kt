package com.island.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun WarningDialog(openAlertDialog: MutableState<Boolean>) {
    if (openAlertDialog.value) {
            AlertDialogGeneric(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    println("Account deleted")
                },
                dialogTitle = "Are you sure?",
                dialogText = "This action is irreversible. Your account will be permanently deleted.",
                icon = Icons.Default.Warning
            )
    }
}

@Composable
fun AccountSection(
    navController: NavController,stateRepository: StateRepository
) {
    val openAlertDeleteDialog = remember { mutableStateOf(false) }
    var email by remember {
        mutableStateOf("")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .padding(4.dp)
    ) {
        if (openAlertDeleteDialog.value) {
            WarningDialog(openAlertDeleteDialog)
        }
        Text(text = "Insert your new email", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier
            .padding(16.dp)
            .align(Alignment.CenterHorizontally))
        CardTextField(label = "Email", text = email, onChange = { email = it })
        ExtendedFloatingActionButton(
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Filled.Check, "Confirm email icon", tint = colorResource(id = R.color.cream)) },
            text = { Text(text = "Change email", color = colorResource(id = R.color.cream)) },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        ExtendedFloatingActionButton(
            onClick = { Route.CHANGE_PASSWORD.open(navController,stateRepository) },
            icon = { Icon(painterResource(id = R.drawable.key), "Key icon", tint = colorResource(id = R.color.cream)) },
            text = { Text(text = "Change password", color = colorResource(id = R.color.cream)) },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        ExtendedFloatingActionButton(
            onClick = { /* TODO */ },
            icon = { Icon(painterResource(id = R.drawable.logout), "Logout icon", tint = colorResource(id = R.color.cream)) },
            text = { Text(text = "Logout", color = colorResource(id = R.color.cream)) },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        ExtendedFloatingActionButton(
            onClick = { openAlertDeleteDialog.value = true },
            icon = { Icon(Icons.Filled.Delete, "Delete icon", tint = colorResource(id = R.color.cream)) },
            text = { Text(text = "Delete account", color = colorResource(id = R.color.cream)) },
            containerColor = Color.Red,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AccountPreview() {
    val navController= rememberNavController()
    Decorations(navController, FAKE_REPOSITORY,Route.ACCOUNT) {
        Account(navController, FAKE_REPOSITORY)
    }
}

@Composable
fun Account(
    navController: NavController,
    stateRepository: StateRepository,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AccountSection(navController,stateRepository)
    }
}
