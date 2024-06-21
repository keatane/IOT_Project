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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/*** Login / Register ***/
val maxWidth = Modifier
    .fillMaxWidth()
    .padding(16.dp, 24.dp)


@Composable
fun CardTextField(
    label: String,
    password: Boolean = false,
    text: String,
    onChange: (String) -> Unit,
    isError: Boolean = false
) {
    var emptyError by rememberSaveable {
        mutableStateOf(false)
    }
    TextField(
        value = text,
        onValueChange = {
            emptyError = it.isEmpty()
            onChange(it)
        },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        singleLine = true,
        isError = isError || emptyError
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
    var confirmError by rememberSaveable {
        mutableStateOf(false)
    }
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.abyss)),
        border = BorderStroke(2.dp, colorResource(id = R.color.rock)),
        modifier = maxWidth
    ) {
        Text(
            text = stringResource(R.string.please_insert_your_credentials),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colorResource(id = R.color.cream)
        )
        CardTextField(
            label = stringResource(R.string.email),
            text = email,
            onChange = { email = it }
        )
        CardTextField(
            label = stringResource(R.string.password),
            password = true,
            text = password,
            onChange = {
                confirmError = false
                password = it
            }
        )
        if (isRegistration) {
            CardTextField(
                label = stringResource(R.string.confirm_password),
                password = true,
                text = confPassword,
                onChange = {
                    confirmError = false
                    confPassword = it
                },
                isError = confirmError
            )
        }
        ExtendedFloatingActionButton(
            onClick = {
                if (password.isEmpty() || (isRegistration && confPassword.isEmpty()) || email.isEmpty()) return@ExtendedFloatingActionButton
                if (isRegistration && confPassword != password) {
                    confirmError = true
                    return@ExtendedFloatingActionButton
                }
                operation(email, password)
            },
            icon = {
                Icon(
                    painterResource(id = R.drawable.login),
                    stringResource(R.string.enter_icon),
                    tint = colorResource(id = R.color.cream)
                )
            },
            text = {
                Text(
                    text = firstButtonMsg,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(24.dp, 0.dp),
                    color = colorResource(id = R.color.cream)
                )
            },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        ExtendedFloatingActionButton(
            onClick = { navigate() },
            icon = {
                Icon(
                    painterResource(id = R.drawable.people),
                    stringResource(R.string.member_icon),
                    tint = colorResource(id = R.color.cream)
                )
            },
            text = {
                Text(
                    text = secondButtonMsg,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(24.dp, 0.dp),
                    color = colorResource(id = R.color.cream)
                )
            },
            containerColor = colorResource(id = R.color.water),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CredentialPage(
    navController: NavController, stateRepository: StateRepository,
    isRegistration: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(-100f) }
    val firebaseToken = MainActivity.getToken()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .offset {
                    IntOffset(
                        offsetX.value.toInt(),
                        offsetY.value.toInt()
                    )
                }
        )

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                launch {
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }

        Column {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(50.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.padding(32.dp, 4.dp),
                    color = colorResource(id = R.color.cream)
                )
                Text(
                    text = if (!isRegistration) stringResource(R.string.login) else stringResource(R.string.sign_up),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp, 0.dp),
                    color = colorResource(id = R.color.cream)
                )
                Spacer(modifier = Modifier.weight(1f))
                CredentialCard(
                    operation = { username, password ->
                        stateRepository.launch {
                            if (isRegistration) {
                                stateRepository.register(username, password, firebaseToken)
                            } else stateRepository.login(username, password, firebaseToken)
                        }
                    },
                    navigate = {
                        if (isRegistration) Route.LOGINPAGE.open(navController) else Route.REGISTERPAGE.open(
                            navController
                        )
                    },
                    isRegistration = isRegistration,
                    firstButtonMsg = if (!isRegistration) stringResource(R.string.login) else stringResource(
                        R.string.sign_up
                    ),
                    secondButtonMsg = if (!isRegistration) stringResource(R.string.not_a_user_sign_up) else stringResource(
                        R.string.already_a_user_sign_in
                    )
                )
                //Button(onClick = { Route.DASHBOARD.open(navController) }) { Text("HomePage") }
            }
        }
    }
}
