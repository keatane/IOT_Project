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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
@Preview
fun RegisterPagePreview() {
    Decorations(
        bottomBarVisible = false
    ) {
        RegisterPage()
    }
}

@Composable
fun RegisterPage(
    register: (String, String) -> Unit = { _, _ -> },
    homePage: () -> Unit = {},
    login: () -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(-100f) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize().offset {
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
                    )}
            }
        }

        ScrollableContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp),
            ) {
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "SmartJugs",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.padding(32.dp, 0.dp)
                )
                Text(
                    text = "Register",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp, 0.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                CredentialCard(
                    operation = register,
                    navigate = login,
                    isRegistration = true,
                    firstButtonMsg = "Create an account",
                    secondButtonMsg = "Already a user? Login"
                )
                Button(onClick = { homePage() }) { Text("HomePage") }
            }
        }
    }
}
