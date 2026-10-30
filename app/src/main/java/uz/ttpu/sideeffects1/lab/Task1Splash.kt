package uz.ttpu.sideeffects1.lab

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 1-topshiriq. Splash ekrani
 * Maqsad: LaunchedEffect va rememberUpdatedState bilan eskirmagan callback'ni chaqirish.
 */
@Composable
fun Task1Screen() {
    var version by remember { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf<List<String>>(emptyList()) }

    // Ota composable'da version hisoblagichi har 500 ms da oshib boradi
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            version++
        }
    }

    var showSplash by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "1-topshiriq: Splash ekrani (rememberUpdatedState)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(text = "Ota composable versiyasi (har 500ms yangilanadi): v$version")

        Button(onClick = {
            showSplash = !showSplash
            if (showSplash) logs = emptyList()
        }) {
            Text(text = if (showSplash) "Splash'ni Qayta Boshlash" else "Splash'ni Ko'rsatish")
        }

        if (showSplash) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1-variant: Direct lambda (eski lambdani ushlab qoladi)
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Direct Lambda (Xato)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Eski lambdani (v0) chaqiradi.", fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LandingScreenDirect(onTimeout = {
                            val msg = "Direct callback bajarildi: v$version"
                            Log.d("Task1Splash", msg)
                            logs = logs + msg
                        })
                    }
                }

                // 2-variant: rememberUpdatedState (eng so'nggi lambdani chaqiradi)
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("rememberUpdatedState (To'g'ri)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Eng so'nggi lambdani chaqiradi.", fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LandingScreenUpdatedState(onTimeout = {
                            val msg = "UpdatedState callback bajarildi: v$version"
                            Log.d("Task1Splash", msg)
                            logs = logs + msg
                        })
                    }
                }
            }
        }

        Text("Logcat Natijalari:", fontWeight = FontWeight.Bold)
        logs.forEach { logText ->
            Text(text = "• $logText", fontSize = 12.sp)
        }

        /*
         * Izoh: Nega LaunchedEffect(onTimeout) ga o'zgartirish noto'g'ri?
         *
         * Gar LaunchedEffect(onTimeout) deb yozilsa, har safar ota composable recompose bo'lganda
         * va yangi lambda yaratilganda (har 500ms da), LaunchedEffect kaliti o'zgargani uchun
         * 2 soniyalik kutish taymeri BEKOR QILINADI va QAYTADAN boshlanadi. Natijada delay(2000)
         * hech qachon oxiriga yetmaydi va onTimeout() umuman chaqirilmaydi.
         *
         * rememberUpdatedState esa taymerni qayta boshlamasdan, effect ichida doimo eng so'nggi
         * onTimeout lambdasiga havola (reference) bo'lishini ta'minlaydi.
         */
    }
}

/**
 * Lambdani to'g'ridan-to'g'ri ushlaydigan variant.
 * LaunchedEffect(true) 1 marta ishlaydi va o'sha paytdagi (v0) onTimeout lambdasini captured qiladi.
 */
@Composable
fun LandingScreenDirect(onTimeout: () -> Unit) {
    var status by remember { mutableStateOf("Kutilmoqda (2s)...") }

    LaunchedEffect(true) {
        delay(2000)
        status = "Tugadi!"
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(status, fontSize = 12.sp)
    }
}

/**
 * rememberUpdatedState ishlatadigan variant.
 * Effect 2 soniyadan so'ng currentOnTimeout() ni chaqirganda eng yangi lambda bajariladi.
 */
@Composable
fun LandingScreenUpdatedState(onTimeout: () -> Unit) {
    var status by remember { mutableStateOf("Kutilmoqda (2s)...") }
    val currentOnTimeout by rememberUpdatedState(onTimeout)

    LaunchedEffect(true) {
        delay(2000)
        status = "Tugadi!"
        currentOnTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(status, fontSize = 12.sp)
    }
}
