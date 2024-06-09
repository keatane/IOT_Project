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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun DeleteJugDialog(
    openDeleteDialog: MutableState<Boolean>,
    id: Int,
    deleteJug: (Int) -> Unit
) {
    AlertDialogGeneric(
        onDismissRequest = { openDeleteDialog.value = false },
        onConfirmation = {
            openDeleteDialog.value = false
            deleteJug(id)
        },
        dialogTitle = "Are you sure?",
        dialogText = "This action is irreversible. You will have to pair again the jug if you remove it.",
        icon = Icons.Default.Warning
    )
}

@Composable
fun RenameJugDialog(
    openAlertDialog: MutableState<Boolean>,
    id: Int, renameJug: (Int, String) -> Unit
) {
    DialogGeneric(
        onDismissRequest = { openAlertDialog.value = false },
        onConfirmation = {
            openAlertDialog.value = false
            renameJug(id, it)
        },
        dialogTitle = "Rename jug",
        icon = Icons.Default.Edit
    )
}

@Composable
fun FilterDialog(
    openFilterDialog: MutableState<Boolean>,
    id: Int,
    changeFilter: (Int, Int) -> Unit
) {
    DialogGeneric(
        onDismissRequest = { openFilterDialog.value = false },
        onConfirmation = {
            openFilterDialog.value = false
            changeFilter(id, it.toInt())
        },
        dialogTitle = "Edit filter capacity",
        icon = Icons.Default.Edit
    )
}

@Composable
fun Jug(
    id: Int,
    title: String,
    changeFilter: (Int, Int) -> Unit,
    dashboardPage: () -> Unit,
    deleteJug: (Int) -> Unit, renameJug: (Int, String) -> Unit,
    selectJug: (Int) -> Unit,
) {
    val openDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val openRenameDialog = rememberSaveable { mutableStateOf(false) }
    val openFilterDialog = rememberSaveable { mutableStateOf(false) }

    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = if (id % 2 == 0) R.color.seaside else R.color.abyss)),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp)
    ) {
        if (openDeleteDialog.value) {
            DeleteJugDialog(openDeleteDialog, id, deleteJug)
        }
        if (openRenameDialog.value) {
            RenameJugDialog(openRenameDialog, id, renameJug)
        }
        if (openFilterDialog.value) {
            FilterDialog(openFilterDialog, id, changeFilter)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                color = colorResource(id = R.color.cream),
                maxLines = 1
            )
            IconButton(onClick = { selectJug(id); dashboardPage() }) {
                Icon(Icons.Outlined.Info, contentDescription = "Observe", tint = colorResource(id = R.color.cream))
            }
            IconButton(onClick = { openRenameDialog.value = true }) {
                Icon(Icons.Outlined.Create, contentDescription = "Rename", tint = colorResource(id = R.color.cream))
            }
            IconButton(onClick = { openFilterDialog.value = true }) {
                Icon(Icons.Outlined.Build, contentDescription = "Change filter", tint = colorResource(id = R.color.cream))
            }
            IconButton(onClick = { openDeleteDialog.value = true }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete jug", tint = colorResource(id = R.color.cream))
            }
        }
    }
}

@Composable
fun JugsSection(
    searchJugs: () -> Unit,
    changeFilter: (Int, Int) -> Unit,
    dashboardPage: () -> Unit, sampleJugsList: List<JugElement>,
    renameJug: (Int, String) -> Unit, deleteJug: (Int) -> Unit, selectJug: (Int) -> Unit
) {
    Column {
        for ((i, jug) in sampleJugsList.withIndex()) {
            Jug(
                i,
                jug.title ?: "i don't know",
                changeFilter,
                dashboardPage,
                deleteJug,
                renameJug,
                selectJug
            )
        }
        ExtendedFloatingActionButton(
            onClick = { searchJugs() },
            icon = { Icon(painterResource(id = R.drawable.wifi), "WiFi icon", tint = colorResource(id = R.color.cream)) },
            text = { Text(text = "Pair a new jug", color = colorResource(id = R.color.cream)) },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
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
    changeFilter: (Int, Int) -> Unit = { _, _ -> },
    dashboardPage: () -> Unit = {},
    jugList: List<JugElement> = listOf(),
    renameJug: (Int, String) -> Unit = { _, _ -> },
    deleteJug: (Int) -> Unit = {}, selectJug: (Int) -> Unit = {}
) {
    initJugs()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 32.dp)
    ) {
        JugsSection(
            searchJugs,
            changeFilter,
            dashboardPage,
            jugList,
            renameJug,
            deleteJug,
            selectJug
        )
    }
}

