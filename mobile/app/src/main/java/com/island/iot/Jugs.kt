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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun DeleteJugDialog(
    openDeleteDialog: MutableState<Boolean>,
    deleteJug: () -> Unit
) {
    ConfirmDialog(
        onConfirmation = deleteJug,
        dialogTitle = "Are you sure?",
        dialogText = "This action is irreversible. You will have to pair again the jug if you remove it.",
        icon = Icons.Default.Warning,
        visibleState = openDeleteDialog
    )
}

@Composable
fun RenameJugDialog(
    openAlertDialog: MutableState<Boolean>,
    renameJug: (String) -> Unit
) {
    PromptDialog(
        onConfirmation = renameJug,
        dialogTitle = "Rename jug",
        icon = Icons.Default.Edit,
        visibleState = openAlertDialog
    )
}

@Composable
fun FilterDialog(
    openFilterDialog: MutableState<Boolean>,
    changeFilter: (Int) -> Unit
) {
    PromptDialog(
        onConfirmation = {
            changeFilter(it.toInt())
        },
        dialogTitle = "Edit filter capacity",
        icon = Icons.Default.Edit,
        visibleState = openFilterDialog
    )
}

@Composable
fun Jug(
    index: Int,
    jug: JugElement,
    title: String,
    changeFilter: (JugElement, Int) -> Unit,
    dashboardPage: () -> Unit,
    deleteJug: (JugElement) -> Unit, renameJug: (JugElement, String) -> Unit,
    selectJug: (JugElement) -> Unit,
) {
    val openDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val openRenameDialog = rememberSaveable { mutableStateOf(false) }
    val openFilterDialog = rememberSaveable { mutableStateOf(false) }

    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = if (index % 2 == 0) R.color.seaside else R.color.abyss)),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp)
    ) {
        if (openDeleteDialog.value) {
            DeleteJugDialog(openDeleteDialog) { deleteJug(jug) }
        }
        if (openRenameDialog.value) {
            RenameJugDialog(openRenameDialog) { renameJug(jug, it) }
        }
        if (openFilterDialog.value) {
            FilterDialog(openFilterDialog) { changeFilter(jug, it) }
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
            IconButton(onClick = { selectJug(jug); dashboardPage() }) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Observe",
                    tint = colorResource(id = R.color.cream)
                )
            }
            IconButton(onClick = { openRenameDialog.value = true }) {
                Icon(
                    Icons.Outlined.Create,
                    contentDescription = "Rename",
                    tint = colorResource(id = R.color.cream)
                )
            }
            IconButton(onClick = { openFilterDialog.value = true }) {
                Icon(
                    Icons.Outlined.Build,
                    contentDescription = "Change filter",
                    tint = colorResource(id = R.color.cream)
                )
            }
            IconButton(onClick = { openDeleteDialog.value = true }) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete jug",
                    tint = colorResource(id = R.color.cream)
                )
            }
        }
    }
}

@Composable
fun JugsSection(
    navController: NavController, repository: StateRepository
) {
    val pairing = MainActivity.getPairing()
    val jugList by repository.jugList.collectAsState()
    Column {
        for ((index, jug) in jugList.withIndex()) {
            Jug(
                index,
                jug,
                jug.title ?: "i don't know",
                changeFilter = { jugId, filter ->
                    repository.launch {
                        repository.changeFilter(jugId, filter)
                    }
                },
                dashboardPage = {
                    Route.DASHBOARD.open(navController)
                },
                renameJug = { id, name ->
                    repository.launch {
                        repository.renameJug(id, name)
                    }
                },
                deleteJug = { repository.launch { repository.deleteJug(it) } },
                selectJug = { repository.setSelectedJug(it) }
            )
        }
        ExtendedFloatingActionButton(
            onClick = { repository.launch { repository.pairJug(pairing) } },
            icon = {
                Icon(
                    painterResource(id = R.drawable.wifi),
                    "WiFi icon",
                    tint = colorResource(id = R.color.cream)
                )
            },
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
    val controller = rememberNavController()
    Decorations(controller, Route.JUGS) {
        Jugs(controller, FAKE_REPOSITORY)
    }
}

@Composable
fun Jugs(
    navController: NavController, stateRepository: StateRepository
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 32.dp)
    ) {
        JugsSection(
            navController, stateRepository
        )
    }
}

