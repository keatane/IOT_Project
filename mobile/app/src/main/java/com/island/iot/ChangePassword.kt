package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
    var oldPassword by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val changePasswordDialog = remember{ mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .padding(4.dp)
    ) {
        Text(
            text = stringResource(R.string.account_password),
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
                text = stringResource(R.string.insert_your_passwords),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.cream),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            CardTextField(password = true, label = stringResource(R.string.old_password), text = oldPassword, onChange = { oldPassword = it })
            CardTextField(password = true, label = stringResource(R.string.new_password), text = password, onChange = { password = it })
        }
        ActionButton(
            icon = Icons.Filled.Check,
            contentDescription = stringResource(R.string.confirm_password_icon),
            text = stringResource(id = R.string.change_password),
        ) {
            if (oldPassword.isEmpty() || password.isEmpty()) return@ActionButton
            changePasswordDialog.value=true
        }
        ActionButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.return_to_account_details_icon),
            text = stringResource(R.string.return_to_account_details)
        ) {
            Route.ACCOUNT.open(navController)
        }
        WarningDialog(changePasswordDialog, stringResource(R.string.irreversibly_password)) {
            stateRepository.launch { stateRepository.changePassword(oldPassword, password) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    val navController = rememberNavController()
    Decorations(
        navController, Route.CHANGE_PASSWORD
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
