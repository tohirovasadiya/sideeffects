package uz.ttpu.sideeffects1.lab

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tashqi Analytics obyektini taqlid qiluvchi obyekt
 */
object Analytics {
    var userType: String = "Guest"
}

/**
 * 4-topshiriq. SideEffect va analytics
 * Maqsad: Compose state'ni tashqi obyektga xavfsiz uzatish.
 */
@Composable
fun Task4Screen() {
    var userType by remember { mutableStateOf("Guest") }
    var dummyRecomposeTrigger by remember { mutableIntStateOf(0) }
    var sideEffectRunCount by remember { mutableIntStateOf(0) }

    // SideEffect har safar ushbu Composable muvaffaqiyatli recompose bo'lib commit qilinganda ishlaydi
    SideEffect {
        Analytics.userType = userType
        sideEffectRunCount++
        Log.d("AnalyticsSideEffect", "SideEffect ishga tushdi! Analytics.userType = '${Analytics.userType}', Jami ishlashlar soni: $sideEffectRunCount")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "4-topshiriq: SideEffect va Analytics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Hozirgi foydalanuvchi turi: $userType", fontWeight = FontWeight.Bold)
                Text(text = "Analytics.userType qiymati: ${Analytics.userType}")
                Text(
                    text = "SideEffect bajarilishlar soni: $sideEffectRunCount",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { userType = if (userType == "Guest") "Premium" else "Guest" },
                modifier = Modifier.weight(1f)
            ) {
                Text("UserType O'zgartirish", fontSize = 12.sp)
            }

            Button(
                onClick = { dummyRecomposeTrigger++ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Boshqa Recompose (#$dummyRecomposeTrigger)", fontSize = 12.sp)
            }
        }

        /*
         * Izohlar va javoblar:
         * 1. Nega state o'zgarmasa ham recomposition'dan keyin SideEffect ishlaydi?
         *    - SideEffect kalitlarga ega emas! U har bir muvaffaqiyatli recomposition (commit)dan keyin
         *      avtomatik ravishda bajariladi. Agar ota composable recompose bo me o'z child'ini ham
         *      recompose qilsa, state o'zgarmagan bo'lsa ham SideEffect qayta ishlaydi.
         *
         * 2. Attempt modelini tushuntiruvchi jumla:
         *    - Direct call (oddiy funksiya chaqiruvi) recomposition bekor qilingan (discard) taqdirda ham
         *      tashqi obyekta ta'sir o'tkazishi mumkin, SideEffect esa faqat va faqat Composition muvaffaqiyatli
         *      bajarilib (commits=true), UI daraxtiga qo'shilgandan so'nggina ishlaydi.
         */
    }
}
