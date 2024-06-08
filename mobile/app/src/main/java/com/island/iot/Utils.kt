package com.island.iot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


/*** General composable functions ***/
@Composable
fun ScrollableContent(
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 32.dp),
    ) { content() }
}

/*** Login / Register ***/
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
        keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        singleLine = true
    )
}

@Composable
fun CredentialCard(
    operation: (String, String) -> Unit,
    navigate: () -> Unit,
    isRegistration: Boolean,
    firstButtonMsg: String,
    secondButtonMsg: String
) {
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
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.water)),
        //border = BorderStroke(2.dp, Color.White),
        modifier = maxWidth
    ) {
        Text(
            text = "Please insert your credentials",
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        CardTextField(label = "Email", text = email, onChange = { email = it })
        CardTextField(
            label = "Password",
            password = true,
            text = password,
            onChange = { password = it }
        )
        if(isRegistration) {
            CardTextField(
                label = "Confirm Password",
                password = true,
                text = confPassword,
                onChange = { confPassword = it }
            )
        }
        Button(
            onClick = { operation(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.ocean), contentColor = Color.White),
            contentPadding = PaddingValues(64.dp, 0.dp),
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = firstButtonMsg, fontSize = 16.sp)
        }
        Button(
            onClick = { navigate()},
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.ocean), contentColor = Color.White),
            contentPadding = PaddingValues(64.dp, 0.dp),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = secondButtonMsg, fontSize = 16.sp)
        }
    }
}

/*** Dialogs ***/
@Composable
fun AlertDialogGeneric(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Warning icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun DialogGeneric(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    icon: ImageVector,
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Input icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Input") }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(text)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}