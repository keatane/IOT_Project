package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
fun WarningDialog(openAlertDialog: MutableState<Boolean>,dialogText:String="",onConfirm:()->Unit) {
    ConfirmDialog(
        onConfirmation = {
            onConfirm()
        },
        dialogTitle = "Are you sure?",
        dialogText = dialogText,
        icon = Icons.Default.Warning, visibleState = openAlertDialog
    )
}

@Composable
fun AccountSection(
    navController: NavController, stateRepository: StateRepository
) {
    val deleteAccountDialog = remember { mutableStateOf(false) }
    val logoutDialog=remember{ mutableStateOf(false) }
    val changeEmailDialog=remember{ mutableStateOf(false) }
    var email by remember {
        mutableStateOf("")
    }
    ScrollableContent {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp)
        ) {
            Text(
                text = "Account details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            OutlinedCard(
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.abyss)),
                border = BorderStroke(2.dp, colorResource(id = R.color.rock)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Insert your new email",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.cream),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                CardTextField(label = "Email", text = email, onChange = { email = it })
            }
            ActionButton(
                icon = Icons.Filled.Check,
                contentDescription = "Confirm email icon",
                text = "Change email"
            ) {
                if(email.isEmpty())return@ActionButton
                changeEmailDialog.value = true
            }
            ActionButton(
                icon = painterResource(id = R.drawable.key),
                contentDescription = "Key icon",
                text = "Change password"
            ) {
                Route.CHANGE_PASSWORD.open(navController)
            }
            ActionButton(
                icon = painterResource(id = R.drawable.logout),
                contentDescription = "Logout icon",
                text = "Logout"
            ) {
                logoutDialog.value = true
            }
            ActionButton(
                icon = Icons.Filled.Delete,
                contentDescription = "Delete icon",
                text = "Delete account",
                buttonColor = Color.Red
            ) {
                deleteAccountDialog.value = true
            }
        }
    }
    WarningDialog(changeEmailDialog, "Your email will be irreversibly changed.") {
        stateRepository.launch { stateRepository.changeEmail(email) }
    }
    WarningDialog(deleteAccountDialog,"This action is irreversible. Your account will be permanently deleted.") {
        stateRepository.launch { stateRepository.deleteAccount() }
    }
    WarningDialog(logoutDialog, "This will disconnect your from the app. You will have to login again.") {
        stateRepository.launch { stateRepository.logout()}
    }
}


@Preview(showBackground = true)
@Composable
fun AccountPreview() {
    val navController = rememberNavController()
    Decorations(navController, Route.ACCOUNT) {
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
        AccountSection(navController, stateRepository)
    }
}
