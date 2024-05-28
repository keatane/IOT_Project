package com.island.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    passwordPage: () -> Unit
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    var email by remember {
        mutableStateOf("")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .padding(4.dp)
    ) {
        if (openAlertDialog.value) {
            WarningDialog(openAlertDialog)
        }
        CardTextField(label = "Email", text = email, onChange = { email = it })
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "Change email")
        }
        Button(
            onClick = { passwordPage() },
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "Change password")
        }
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "Disconnect")
        }
        Button(
            onClick = { openAlertDialog.value = true },
            modifier = Modifier.padding(64.dp),
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text(text = "Delete account")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AccountPreview() {
    Decorations {
        Account()
    }
}

@Composable
fun Account(
    initAccount: () -> Unit = {},
    passwordPage: () -> Unit = {},
) {
    initAccount()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ScrollableContent {
            AccountSection(passwordPage)
        }
    }
}
