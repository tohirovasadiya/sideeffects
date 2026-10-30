package uz.ttpu.sideeffects1.lab

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

sealed class ImageResult {
    object Loading : ImageResult()
    data class Success(val data: String) : ImageResult()
    data class Error(val message: String) : ImageResult()
}

/**
 * Soxta repozitoriy
 */
object FakeRepository {
    suspend fun load(url: String): ImageResult {
        delay(500) // 500 ms kechikish
        return if (url.endsWith(".png", ignoreCase = true)) {
            ImageResult.Success("Rasm muvaffaqiyatli yuklandi: $url")
        } else {
            ImageResult.Error("Xatolik: URL '.png' bilan tugashi shart! ($url)")
        }
    }
}

/**
 * produceState yordamida rasm yuklovchi composable funksiya.
 * Kichik harf bilan boshlangan funksiya, chunki u State qaytaradi.
 */
@Composable
fun loadNetworkImage(url: String): State<ImageResult> {
    return produceState<ImageResult>(initialValue = ImageResult.Loading, key1 = url) {
        value = ImageResult.Loading // Key o'zgarganda Loading ko'rsatiladi
        Log.d("Task6ProduceState", "Producer ISHGA TUSHDI (URL: $url)")

        try {
            val result = FakeRepository.load(url)
            value = result // Qiymat beriladi (bir xil qiymat berilsa conflates bo'ladi, recompose bo'lmaydi)
            Log.d("Task6ProduceState", "Producer MUVAFFAQIYATLI TUGADI: $result")
        } catch (e: CancellationException) {
            Log.d("Task6ProduceState", "Producer BEKOR QILINDI (CancellationException)")
            throw e
        } finally {
            Log.d("Task6ProduceState", "finally: Producer tozalando / bekor qilindi (URL: $url)")
        }
    }
}

/**
 * 6-topshiriq (Bonus). produceState
 */
@Composable
fun Task6Screen() {
    var url by remember { mutableStateOf("https://example.com/photo.png") }
    var isVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "6-topshiriq (Bonus): produceState bilan rasm yuklash",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Composable Composition'da:")
            Switch(
                checked = isVisible,
                onCheckedChange = { isVisible = it }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Joriy URL: $url", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { url = "https://example.com/avatar.png" },
                modifier = Modifier.weight(1f)
            ) {
                Text("Muvaffaqiyatli (.png)", fontSize = 11.sp)
            }

            Button(
                onClick = { url = "https://example.com/file.jpg" },
                modifier = Modifier.weight(1f)
            ) {
                Text("Xatolik (.jpg)", fontSize = 11.sp)
            }

            Button(
                onClick = { url = "https://example.com/avatar.png" }, // Bir xil URL
                modifier = Modifier.weight(1f)
            ) {
                Text("Bir xil URL", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isVisible) {
            val imageState by loadNetworkImage(url = url)

            // UI when bilan uchala holatni ko'rsatadi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (val result = imageState) {
                        is ImageResult.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Loading...", fontWeight = FontWeight.Bold)
                            }
                        }
                        is ImageResult.Success -> {
                            Text(
                                text = result.data,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is ImageResult.Error -> {
                            Text(
                                text = result.message,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Composable tark etdi! Logcat'da 'finally' yozuvini ko'ring.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
