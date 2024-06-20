package com.island.iot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch


/*** General composable functions ***/
@Composable
fun ScrollableContent(
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) { content() }
}

/*** Dialogs ***/
@Composable
fun GenericDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    icon: ImageVector,
    dialogText: @Composable () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Warning icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            dialogText()
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun BlockingDialog(dialogTitle:String){
    AlertDialog(
        onDismissRequest = { },
        title={Text(text=dialogTitle)},
        confirmButton = {}
    )
}


@Composable
fun AutoCloseDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Unit,
    dialogTitle: String,
    icon: ImageVector,
    visibleState: MutableState<Boolean>,
    dialogText: @Composable () -> Unit,
) {
    var visible by visibleState
    if (visible) {
        GenericDialog(
            icon = icon,
            dialogTitle = dialogTitle,
            onDismissRequest = {
                visible = false
                onDismissRequest()
            },
            onConfirmation = {
                visible = false
                onConfirmation()
            },
            dialogText = dialogText
        )
    }
}

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit = {},
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    visibleState: MutableState<Boolean>,
    onConfirmation: () -> Unit,
) {
    AutoCloseDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        dialogTitle = dialogTitle,
        icon = icon,
        visibleState = visibleState,
    ) {
        Text(text = dialogText)
    }
}

@Composable
fun PromptDialog(
    onDismissRequest: () -> Unit = {},
    dialogTitle: String,
    icon: ImageVector,
    visibleState: MutableState<Boolean>,
    onConfirmation: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    AutoCloseDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = { onConfirmation(text) },
        dialogTitle = dialogTitle,
        icon = icon,
        visibleState = visibleState
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Input") },
            singleLine = true
        )
    }
}


@Composable
fun ActionButton(
    text: String, onClick: () -> Unit,
    buttonColor: Color = colorResource(id = R.color.water),
    icon: @Composable () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            icon()
        },
        text = { Text(text = text, color = colorResource(id = R.color.cream), modifier = Modifier.padding(24.dp, 0.dp)) },
        containerColor = buttonColor,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ActionButton(icon: Painter, contentDescription: String, text: String, onClick: () -> Unit) {
    ActionButton(text, onClick) {
        Icon(
            icon,
            contentDescription,
            tint = colorResource(id = R.color.cream)
        )
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    text: String,
    buttonColor: Color = colorResource(id = R.color.water),
    onClick: () -> Unit
) {
    ActionButton(text, onClick,buttonColor) {
        Icon(
            icon,
            contentDescription,
            tint = colorResource(id = R.color.cream),
        )
    }
}