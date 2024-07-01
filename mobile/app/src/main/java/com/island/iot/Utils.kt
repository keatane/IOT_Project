package com.island.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp


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
            Icon(icon, contentDescription = stringResource(R.string.warning_icon))
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
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun BlockingDialog(dialogTitle: String) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = dialogTitle) },
        confirmButton = {}
    )
}


@Composable
fun AutoCloseDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Boolean,
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
                visible = !onConfirmation()
            },
            dialogText = dialogText
        )
    }
}

@Composable
fun AlertDialog(
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = stringResource(R.string.warning_icon))
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(dialogText)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.ok))
            }
        },
    )
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
        onConfirmation = { onConfirmation();true },
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
    numeric: Boolean = false,
    password: Boolean = false,
    onConfirmation: (String) -> Boolean,
) {
    var emptyError by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    AutoCloseDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            text.isNotEmpty() && onConfirmation(text)
        },
        dialogTitle = dialogTitle,
        icon = icon,
        visibleState = visibleState,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                emptyError = it.isEmpty()
                text = it
            },
            label = { Text(stringResource(R.string.input)) },
            singleLine = true,
            isError = emptyError,
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.NumberPassword) else if (password) KeyboardOptions(
                keyboardType = KeyboardType.Password
            ) else KeyboardOptions.Default
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
        text = {
            Text(
                text = text,
                color = colorResource(id = R.color.cream),
                modifier = Modifier.padding(18.dp, 0.dp)
            )
        },
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
    ActionButton(text, onClick, buttonColor) {
        Icon(
            icon,
            contentDescription,
            tint = colorResource(id = R.color.cream),
        )
    }
}
