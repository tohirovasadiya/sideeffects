package uz.ttpu.sideeffects1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.sideeffects1.lab.*
import uz.ttpu.sideeffects1.ui.theme.SideEffects1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SideEffects1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LabMainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LabMainScreen(modifier: Modifier = Modifier) {
    var selectedTaskIndex by remember { mutableIntStateOf(0) }
    val tasks = listOf(
        "1-Splash",
        "2-Snackbar",
        "3-Lifecycle",
        "4-Analytics",
        "5-Scroll",
        "6-produceState"
    )

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTaskIndex,
            edgePadding = 8.dp
        ) {
            tasks.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTaskIndex == index,
                    onClick = { selectedTaskIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTaskIndex) {
                0 -> Task1Screen()
                1 -> Task2Screen()
                2 -> Task3Screen()
                3 -> Task4Screen()
                4 -> Task5Screen()
                5 -> Task6Screen()
            }
        }
    }
}
