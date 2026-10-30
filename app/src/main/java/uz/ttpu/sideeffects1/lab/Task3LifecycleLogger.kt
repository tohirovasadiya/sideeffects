package uz.ttpu.sideeffects1.lab

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * 3-topshiriq. Lifecycle logger
 * Maqsad: DisposableEffect bilan listener qo'shish va olib tashlash.
 */
@Composable
fun Task3Screen() {
    var isObserverActive by remember { mutableStateOf(true) }
    var logs by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "3-topshiriq: LifecycleLogger va DisposableEffect",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lifecycle Observer Aktivlashtirish:")
            Switch(
                checked = isObserverActive,
                onCheckedChange = { isObserverActive = it }
            )
        }

        if (isObserverActive) {
            LifecycleLoggerComponent(onLog = { msg ->
                logs = logs + msg
            })
        } else {
            Text(
                text = "Observer o'chirildi (Composition'dan chiqdi). Logcat'da removeObserver yozuvini ko'ring.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Observer Voqealar Tarixi (Logs):", fontWeight = FontWeight.Bold)
        logs.takeLast(10).forEach { logMsg ->
            Text(text = "• $logMsg", fontSize = 12.sp)
        }

        /*
         * Izohlar:
         * 1. Nega onDispose majburiy?
         *    - DisposableEffect oxirida onDispose { ... } bloki bo'lishi MAJBURIDIR.
         *    - Gar onDispose olib tashlansa, Kotlin kompilyatori xatolik beradi (DisposableEffect scope requires onDispose clause).
         *    - onDispose bo'lmasa observer xotirada qolib ketadi (memory leak yuzaga keladi).
         *
         * 2. Switch'ni 5 marta almashtirganda:
         *    - addObserver va removeObserver chaqirilishlar soni teng bo'ladi (har safar yangi qo'shilganda eski tozalab tashlanadi).
         */
    }
}

@Composable
fun LifecycleLoggerComponent(onLog: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_STOP) {
                val msg = "Lifecycle Event: $event"
                Log.d("LifecycleLogger", msg)
                onLog(msg)
            }
        }

        // Observer qo'shiladi
        lifecycleOwner.lifecycle.addObserver(observer)
        val addMsg = "addObserver chaqirildi (Observer qo'shildi)"
        Log.d("LifecycleLogger", addMsg)
        onLog(addMsg)

        // Composable tark etilganda yoki lifecycleOwner o'zgarganda tozalanadi
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            val removeMsg = "removeObserver chaqirildi (Observer olib tashlandi)"
            Log.d("LifecycleLogger", removeMsg)
            onLog(removeMsg)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = "Lifecycle Observer aktiv holatda. Ilovani background'ga o'tkazib yoki Switch'ni o'chirib tekshiring.",
            modifier = Modifier.padding(16.dp),
            fontSize = 13.sp
        )
    }
}
