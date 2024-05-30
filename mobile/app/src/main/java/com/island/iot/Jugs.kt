package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class JugElement(val title: String)

var selectedId = 0
var sampleJugsList = mutableListOf(
    JugElement(title = "Kitchen Jug"),
    JugElement(title = "Living Room Jug")
)


@Composable
fun DeleteJugDialog(
    openAlertDialog: MutableState<Boolean>,
) {
    if (openAlertDialog.value) {
        AlertDialogGeneric(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = {
                openAlertDialog.value = false
                sampleJugsList.remove(sampleJugsList[selectedId]) // Still doesn't visually remove it
                println("Jug deleted")
            },
            dialogTitle = "Are you sure?",
            dialogText = "This action is irreversible. You will have to pair again the jug if you remove it.",
            icon = Icons.Default.Warning
        )
    }
}

@Composable
fun RenameJugDialog(
    openAlertDialog: MutableState<Boolean>,
) {
    if (openAlertDialog.value) {
        DialogGeneric(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = {
                openAlertDialog.value = false
                // ???
                println("Jug renamed")
            },
            dialogTitle = "Rename jug",
            //dialogText = "Provide the new name for the jug.",
            icon = Icons.Default.Edit
        )
    }
}

@Composable
fun Jug(id: Int, title: String) {
    val openDeleteDialog = remember { mutableStateOf(false) }
    val openRenameialog = remember { mutableStateOf(false) }

    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp)
    ) {
        if (openDeleteDialog.value) {
            DeleteJugDialog(openDeleteDialog)
        }
        if (openRenameialog.value) {
            RenameJugDialog(openRenameialog)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title, modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            IconButton(onClick = { openRenameialog.value = true; selectedId = id }) {
                Icon(Icons.Outlined.Create, contentDescription = "Rename")
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Outlined.Build, contentDescription = "Change filter")
            }
            IconButton(onClick = { openDeleteDialog.value = true; selectedId = id }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete jug")
            }
        }
    }
}

@Composable
fun Section(searchJugs: () -> Unit) {
    val jugList = remember { sampleJugsList }
    Column {
        for ((i, jug) in jugList.withIndex()) {
            Jug(i, jug.title)
        }
        Button(
            onClick = { searchJugs() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Pair a new jug")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun JugsPreview() {
    Decorations {
        Jugs()
    }
}

@Composable
fun Jugs(
    initJugs: () -> Unit = {}, searchJugs: () -> Unit = {}
) {
    initJugs()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 32.dp)
    ) {
        Section(searchJugs)
    }
}

