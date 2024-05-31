package com.island.iot

import android.util.Log
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
import androidx.compose.material.icons.outlined.Info
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

data class JugElement(var title: String, var filter: Int)

var selectedJug = 0
// The sample jug list will be substituted by the actual list of jugs of the user from the database node-red
var sampleJugsList = mutableListOf(
    JugElement(title = "Kitchen Jug", filter = 150),
    JugElement(title = "Living Room Jug", filter = 200)
)

fun fetchJugs() {
    // should call node-red to get the list of jugs
    // sampleJugsList = fetchDB()
    Log.d("Jugs", "Jugs initialized")
}

@Composable
fun DeleteJugDialog(
    openDeleteDialog: MutableState<Boolean>,
    id: Int
) {
    if (openDeleteDialog.value) {
        AlertDialogGeneric(
            onDismissRequest = { openDeleteDialog.value = false },
            onConfirmation = {
                openDeleteDialog.value = false
                sampleJugsList.remove(sampleJugsList[id]) // Still doesn't visually remove it until a refresh
                // should remove also jug from db node-red
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
    id: Int
) {
    if (openAlertDialog.value) {
        DialogGeneric(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = {
                openAlertDialog.value = false
                sampleJugsList[id].title = it // still doesn't visually update it until a refresh
            },
            dialogTitle = "Rename jug",
            icon = Icons.Default.Edit
        )
    }
}

@Composable
fun FilterDialog(
    openFilterDialog: MutableState<Boolean>,
    id: Int,
    changeFilter: (String, Int, Int) -> Unit
) {
    if (openFilterDialog.value) {
        DialogGeneric(
            onDismissRequest = { openFilterDialog.value = false },
            onConfirmation = {
                openFilterDialog.value = false
                sampleJugsList[id].filter = it.toInt()
                changeFilter("username", id, it.toInt()) // should update filter in db node-red
            },
            dialogTitle = "Edit filter capacity",
            icon = Icons.Default.Edit
        )
    }
}

@Composable
fun Jug(id: Int, title: String, changeFilter: (String, Int, Int) -> Unit, dashboardPage: () -> Unit){
    val openDeleteDialog = remember { mutableStateOf(false) }
    val openRenameDialog = remember { mutableStateOf(false) }
    val openFilterDialog = remember { mutableStateOf(false) }

    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp)
    ) {
        if (openDeleteDialog.value) {
            DeleteJugDialog(openDeleteDialog, id)
        }
        if (openRenameDialog.value) {
            RenameJugDialog(openRenameDialog, id)
        }
        if (openFilterDialog.value) {
            FilterDialog(openFilterDialog, id, changeFilter)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title, modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            IconButton(onClick = { selectedJug = id; dashboardPage() }) {
                Icon(Icons.Outlined.Info, contentDescription = "Observe")
            }
            IconButton(onClick = { openRenameDialog.value = true }) {
                Icon(Icons.Outlined.Create, contentDescription = "Rename")
            }
            IconButton(onClick = { openFilterDialog.value = true }) {
                Icon(Icons.Outlined.Build, contentDescription = "Change filter")
            }
            IconButton(onClick = { openDeleteDialog.value = true }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete jug")
            }
        }
    }
}

@Composable
fun JugsSection(
    searchJugs: () -> Unit,
    changeFilter: (String, Int, Int) -> Unit,
    dashboardPage: () -> Unit
) {
    val jugList = remember { sampleJugsList }
    Column {
        for ((i, jug) in jugList.withIndex()) {
            Jug(i, jug.title, changeFilter, dashboardPage)
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
    initJugs: () -> Unit = {},
    searchJugs: () -> Unit = {},
    changeFilter: (String, Int, Int) -> Unit = { _, _, _ -> },
    dashboardPage: () -> Unit = {}
) {
    initJugs()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 32.dp)
    ) {
        JugsSection(searchJugs, changeFilter, dashboardPage)
    }
}

