package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memorygame.ui.theme.MemoryGameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoryGameTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { InicioScreen(navController) }
        composable("memory_game") { GameScreen() }
    }
}

@Composable
fun InicioScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Jogo da Memória!",
                modifier = Modifier.padding(bottom = 50.dp),
                fontSize = 30.sp
            )
            Button(onClick = { navController.navigate("memory_game") }) {
                Text(text = stringResource(R.string.botaoIniciar), fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun GameScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            MemoryGrid(modifier = Modifier.padding(padding))
        }
    )
}

@Composable
fun MemoryGrid(modifier: Modifier = Modifier) {
    val imageResources = listOf(
        R.drawable.iandroid,
        R.drawable.icar,
        R.drawable.iheadphones,
        R.drawable.iphone
    )
    val initialItems by remember { mutableStateOf(imageResources.flatMap { listOf(it, it) }.shuffled()) }
    var revealedIndices by remember { mutableStateOf(listOf<Int>()) }
    var matchedIndices by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Column {
                for (i in 0..3) {
                    MemoryButton(
                        item = initialItems[i],
                        revealed = revealedIndices.contains(i) || matchedIndices.contains(i)
                    ) {
                        handleButtonClick(i, revealedIndices, matchedIndices) {
                            revealedIndices = it
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                for (i in 4..7) {
                    MemoryButton(
                        item = initialItems[i],
                        revealed = revealedIndices.contains(i) || matchedIndices.contains(i)
                    ) {
                        handleButtonClick(i, revealedIndices, matchedIndices) {
                            revealedIndices = it
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(revealedIndices) {
        if (revealedIndices.size == 2) {
            val (firstIndex, secondIndex) = revealedIndices
            if (initialItems[firstIndex] == initialItems[secondIndex]) {
                matchedIndices = matchedIndices + revealedIndices
                revealedIndices = emptyList()
            } else {
                delay(1000)
                revealedIndices = emptyList()
            }
        }
    }
}

fun handleButtonClick(
    index: Int,
    revealedIndices: List<Int>,
    matchedIndices: Set<Int>,
    onClick: (List<Int>) -> Unit
) {
    if (revealedIndices.size < 2 && !revealedIndices.contains(index) && !matchedIndices.contains(index)) {
        val newRevealedIndices = revealedIndices + index
        onClick(newRevealedIndices)
    }
}


@Composable
fun MemoryButton(item: Int, revealed: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .size(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (revealed) Color.Green else Color.Gray
        ),
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.Black)
    ) {
        if (revealed) {
            Image(
                painter = painterResource(id = item),
                contentDescription = "item encontrado",
                modifier = Modifier.size(40.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ieye),
                contentDescription = "item escondido",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MemoryGameTheme {
        AppNavigation()
    }
}
