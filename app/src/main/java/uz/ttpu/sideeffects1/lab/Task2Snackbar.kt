package uz.ttpu.sideeffects1.lab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * 2-topshiriq. Snackbar tugmasi
 * Maqsad: rememberCoroutineScope bilan callback ichidan coroutine ishga tushirish.
 */
@Composable
fun Task2Screen() {
    var isScreenActive by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "2-topshiriq: Snackbar va CoroutineScope",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ekranda qolish / Ekranni tark etish:")
            Switch(
                checked = isScreenActive,
                onCheckedChange = { isScreenActive = it }
            )
        }

        if (isScreenActive) {
            SnackbarDemoContent()
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Boshqa ekranga o'childi (Composable composition'dan chiqdi). CoroutineScope avtomatik bekor qilindi va snackbar qolib ketmadi!",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        /*
         * Izohlar:
         * 1. Nega LaunchedEffect bu yerda ishlamaydi?
         *    - LaunchedEffect faqat Composable composition'ga kirgan paytda (yoki key o'zgarganda)
         *      ishga tushadi. Uni onClick callback (foydalanuvchi tugmani bosganda) ichida chaqirib bo'lmaydi.
         *    - Foydalanuvchi hodisalari (onClick, onDrag va hokazo) uchun callback ichidan coroutine
         *      ochishda rememberCoroutineScope() ishlatiladi.
         *
         * 2. Snackbar tugamaguncha ikkinchi marta bosilsa nima bo'ladi?
         *    - SnackbarHostState navbat mexanizmiga (queue) ega yoki joriy snackbar'ni bekor qilib,
         *      yangi snackbar'ni ko'rsatadi. Avvalgi showSnackbar suspend funksiyasi bekor bo'ladi (cancel qilinadi).
         */
    }
}

@Composable
fun SnackbarDemoContent() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var clickCount by remember { mutableIntStateOf(0) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Snackbar ko'rsatish uchun tugmani bosing", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                clickCount++
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Snackbar #$clickCount ko'rsatildi!",
                        duration = SnackbarDuration.Short
                    )
                }
            }) {
                Text("Snackbar chiqarish (#$clickCount)")
            }
        }
    }
}
