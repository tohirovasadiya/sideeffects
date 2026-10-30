package uz.ttpu.sideeffects1.lab

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 5-topshiriq. Scroll: derivedStateOf va snapshotFlow
 * Maqsad: Recomposition'ni kamaytirish va Compose State'dan Flow olish.
 */
@Composable
fun Task5Screen() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 1-variant: derivedStateOf yordamida (Optimal)
    val showButtonDerived by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // 2-variant: To'g'ridan-to'g'ri o'qish (Non-optimal)
    val showButtonDirect = listState.firstVisibleItemIndex > 0

    // Recomposition hisoblagichlari
    var derivedRecomposeCount by remember { mutableIntStateOf(0) }
    var directRecomposeCount by remember { mutableIntStateOf(0) }
    var analyticsEventCount by remember { mutableIntStateOf(0) }

    // Analytics snapshotFlow monitoring
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                analyticsEventCount++
                Log.d("ScrollAnalytics", "Foydalanuvchi 0-elementdan o'tdi! Jami hodisalar: $analyticsEventCount")
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "5-topshiriq: derivedStateOf va snapshotFlow",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Recomposition statistikasi kartasi
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Recomposition statistikasi:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• derivedStateOf Recomposition soni: $derivedRecomposeCount", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    Text("• Direct Reading Recomposition soni: $directRecomposeCount", color = Color.Red, fontWeight = FontWeight.Bold)
                    Text("• snapshotFlow Analytics hodisalari: $analyticsEventCount", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Izoh: Ro'yxatni pastga surganingizda Direct Reading har bir element o'tganda qayta chiziladi (ko'p recompose bo'ladi), derivedStateOf esa faqat true/false holati o'zgargandagina recompose bo'ladi.",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Direct variant recomposition tracker
            DirectReadingTracker(showButtonDirect = showButtonDirect) {
                directRecomposeCount++
            }

            // DerivedState variant recomposition tracker
            DerivedStateTracker(showButtonDerived = showButtonDerived) {
                derivedRecomposeCount++
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 100 ta elementli LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(100) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = "Element #$index",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Tepaga qaytish tugmasi
        AnimatedVisibility(
            visible = showButtonDerived,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Text(text = "▲ Top", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DirectReadingTracker(showButtonDirect: Boolean, onRecompose: () -> Unit) {
    Text("Direct Holati: $showButtonDirect", fontSize = 10.sp, color = Color.Gray)
    SideEffect {
        onRecompose()
    }
}

@Composable
fun DerivedStateTracker(showButtonDerived: Boolean, onRecompose: () -> Unit) {
    Text("Derived Holati: $showButtonDerived", fontSize = 10.sp, color = Color.Gray)
    SideEffect {
        onRecompose()
    }
}
