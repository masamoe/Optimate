package com.example.optimate.employeeFlow

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.optimate.loginAndRegister.GlobalUserData
import com.example.optimate.loginAndRegister.milliSecondsToHours
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ViewAllPayStubsList(validBiWeeklyDateRanges: List<List<String>>) {
    LazyColumn {
        itemsIndexed(validBiWeeklyDateRanges) { _, worklog ->
            ViewAllPayStubsRow(worklog)
        }
    }
}

@Composable
fun ViewAllPayStubsRow(dateRange: List<String>) {
    var hours by remember { mutableDoubleStateOf(0.0) }
    var income by remember { mutableDoubleStateOf(0.0) }
    val approvedWorkLogs = mutableListOf<Map<String, Any>>()
    val inputDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault())
    val startDate = inputDateFormat.parse(dateRange[0])
    val endDate = inputDateFormat.parse(dateRange[1])
    var isExpanded by remember { mutableStateOf(false) }
    val backgroundColor = Color(0xFFF2EBF3)
    val frameColor = Color(0xFF6750A4)

    val cornerRadius = 12.dp

    LaunchedEffect(dateRange) {
        getWorkedHoursForDateRange(dateRange, approvedWorkLogs) { totalHours, totalIncome ->
            hours = totalHours
            income = totalIncome
        }
    }


    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(3.dp, frameColor, shape = RoundedCornerShape(cornerRadius)),
        colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(cornerRadius),
    ){
        Box(modifier = Modifier.fillMaxSize()){
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                            text = outputDateFormat.format(startDate)+ " to " + outputDateFormat.format(endDate),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                        )

                }
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

        }
    }

    if (isExpanded) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)){
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Total Hours:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$hours hrs", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Blue
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Gross pay:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$$income", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Blue
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Net pay:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${income*0.8}", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Blue
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Taxes:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${income*0.2}", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Blue
                )
            }

            }
        }
    }

}

private fun getWorkedHoursForDateRange(
    dateRange: List<String>,
    approvedWorkLogs: MutableList<Map<String, Any>>,
    onResult: (Double, Double) -> Unit
) {
    val db = Firebase.firestore
    val startDate = SimpleDateFormat("yyyyMMdd").parse(dateRange[0])
    val endDate = SimpleDateFormat("yyyyMMdd").parse(dateRange[1])

    db.collection("totalHours")
        .whereEqualTo("bid", GlobalUserData.bid)
        .get()
        .addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.forEach { documentSnapshot ->
                val workLogs = documentSnapshot.data as Map<String, Any>?
                workLogs?.let {
                    val userLogs = it[GlobalUserData.uid] as? List<Map<String, Any>>
                    userLogs?.forEach { log ->
                        val approved = log["approved"] as Boolean?
                        if (approved == true) {
                            log.keys.filter { it.matches(Regex("\\d{8}")) }.forEach { dateKey ->
                                try {
                                    val logDate = SimpleDateFormat("yyyyMMdd").parse(dateKey)
                                    if (logDate in startDate..endDate) {
                                        val hours = log[dateKey] as? Long ?: 0L
                                        val wage = (log["wage"] as? Number)?.toDouble() ?: 0.0
                                        val logWithUid = mapOf(
                                            "date" to dateKey,
                                            "hours" to hours,
                                            "wage" to wage
                                        )
                                        approvedWorkLogs.add(logWithUid)
                                    }
                                } catch (e: Exception) {
                                    // Handle date parsing exception
                                }
                            }
                        }
                    }
                }
            }

            val (totalHours, totalIncome) = countTotalHoursAndIncome(approvedWorkLogs)
            onResult(totalHours, totalIncome)
        }
        .addOnFailureListener { e ->
            Log.e("ViewAllPayStubs", "Error getting documents: ", e)
        }
}

private fun countTotalHoursAndIncome(
    workLogs: List<Map<String, Any>>
): Pair<Double, Double> {
    var totalHours = 0.0
    var totalIncome = 0.0
    workLogs.forEach { log ->
        val totalHour = log["hours"] as Long
        val wage = log["wage"] as Double
        totalHours += milliSecondsToHours(totalHour)
        totalIncome += milliSecondsToHours(totalHour) * wage
    }
    val totalHoursToTwoDecimalPlaces = String.format("%.2f", totalHours).toDouble()
    val totalIncomeToTwoDecimalPlaces = String.format("%.2f", totalIncome).toDouble()
    return Pair(totalHoursToTwoDecimalPlaces, totalIncomeToTwoDecimalPlaces)
}
