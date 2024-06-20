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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun DeleteJugDialog(
    openDeleteDialog: MutableState<Boolean>,
    deleteJug: () -> Unit
) {
    ConfirmDialog(
        onConfirmation = deleteJug,
        dialogTitle = stringResource(id = R.string.are_you_sure),
        dialogText = stringResource(R.string.irreversible_delete_jug),
        icon = Icons.Default.Warning,
        visibleState = openDeleteDialog
    )
}

@Composable
fun RenameJugDialog(
    openRenameDialog: MutableState<Boolean>,
    renameJug: (String) -> Unit
) {
    PromptDialog(
        onConfirmation = { if (it.length <= 12){renameJug(it);true} else false },
        dialogTitle = stringResource(R.string.rename_jug_12_max),
        icon = Icons.Default.Edit,
        visibleState = openRenameDialog
    )
}

@Composable
fun FilterDialog(
    openFilterDialog: MutableState<Boolean>,
    changeFilter: (Int) -> Unit
) {
    PromptDialog(
        onConfirmation = {
            val number = it.toIntOrNull() ?: return@PromptDialog false
            changeFilter(number)
            return@PromptDialog true
        },
        dialogTitle = stringResource(R.string.edit_filter_capacity),
        icon = Icons.Default.Edit,
        visibleState = openFilterDialog,
        numeric = true
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
                    contentDescription = stringResource(R.string.inspect),
                    tint = colorResource(id = R.color.cream)
                )
            }
            IconButton(onClick = { openRenameDialog.value = true }) {
                Icon(
                    Icons.Outlined.Create,
                    contentDescription = stringResource(R.string.rename_jug),
                    tint = colorResource(id = R.color.cream)
                )
            }
            IconButton(onClick = { openFilterDialog.value = true }) {
                Icon(
                    Icons.Outlined.Build,
                    contentDescription = stringResource(R.string.change_filter),
                    tint = colorResource(id = R.color.cream)
                )
            }
            IconButton(onClick = { openDeleteDialog.value = true }) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete_jug),
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
    val jugList by repository.jugList.collectAsState(listOf())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.your_jugs),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        for ((index, jug) in jugList.withIndex()) {
            Jug(
                index,
                jug,
                jug.name,
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
                selectJug = { repository.launch { repository.setSelectedJug(it) } }
            )
        }
        ExtendedFloatingActionButton(
            onClick = { repository.launch { repository.pairJug(pairing) } },
            icon = {
                Icon(
                    painterResource(id = R.drawable.wifi),
                    stringResource(R.string.wifi_icon),
                    tint = colorResource(id = R.color.cream)
                )
            },
            text = { Text(text = stringResource(R.string.pair_a_new_jug)  , color = colorResource(id = R.color.cream)) },
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
    ScrollableContent {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        ) {
            JugsSection(
                navController, stateRepository
            )
        }
    }
}

