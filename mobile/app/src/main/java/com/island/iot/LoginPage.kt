package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

val maxWidth = Modifier
    .fillMaxWidth()
    .padding(16.dp, 4.dp)

@Composable
fun CardTextField(
    label: String,
    password: Boolean = false,
    text: String,
    onChange: (String) -> Unit
) {

    TextField(
        value = text,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = maxWidth,
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
    )
}

@Composable
fun RegisterCard(register: (String, String) -> Unit) {
    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var confPassword by rememberSaveable {
        mutableStateOf("")
    }
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.Black),
        modifier = maxWidth
    ) {
        Text(text = "Register", modifier = Modifier.padding(16.dp))
        CardTextField(label = "Email", text = email, onChange = { email = it })
        CardTextField(
            label = "Password",
            password = true,
            text = password,
            onChange = { password = it })
        CardTextField(
            label = "Confirm password",
            password = true,
            text = confPassword,
            onChange = { confPassword = it })
        Button(
            onClick = { register(email, password) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Register")
        }
    }
}

@Composable
fun LoginCard(login: (String, String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Login", modifier = Modifier.padding(16.dp))
        CardTextField(
            label = "Email",
            text = email,
            onChange = { email = it },
        )
        CardTextField(
            label = "Password",
            text = password,
            onChange = { password = it },
        )
        Button(onClick = { login(email, password) }, modifier = Modifier.padding(16.dp)) {
            Text(text = "Login")
        }
    }
}

@Composable
@Preview
fun LoginPagePreview() {
    Decorations(bottomBarVisible = false
    ) {
        LoginPage()
    }
}

@Composable
fun LoginPage(
    register: (String, String) -> Unit = { _, _ -> },
    homePage: () -> Unit = {},
    login: (String, String) -> Unit = { _, _ -> }
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Authentication", modifier = Modifier.padding(16.dp))
        RegisterCard(register)
        LoginCard(login)
        Button(onClick = { homePage() }) { Text("HomePage") }
    }
}
