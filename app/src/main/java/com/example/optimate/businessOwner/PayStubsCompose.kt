package com.example.optimate.businessOwner

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.optimate.loginAndRegister.GlobalUserData
import com.example.optimate.loginAndRegister.milliSecondsToHours
import com.example.optimate.loginAndRegister.uidToName
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

val biWeeklyDateRanges2024 = listOf(
    listOf("20240101", "20240115"), listOf("20240116", "20240131"),
    listOf("20240201", "20240215"), listOf("20240216", "20240229"),
    listOf("20240301", "20240315"), listOf("20240316", "20240331"),
    listOf("20240401", "20240415"), listOf("20240416", "20240430"),
    listOf("20240501", "20240515"), listOf("20240516", "20240531"),
    listOf("20240601", "20240615"), listOf("20240616", "20240630"),
    listOf("20240701", "20240715"), listOf("20240716", "20240731"),
    listOf("20240801", "20240815"), listOf("20240816", "20240831"),
    listOf("20240901", "20240915"), listOf("20240916", "20240930"),
    listOf("20241001", "20241015"), listOf("20241016", "20241031"),
    listOf("20241101", "20241115"), listOf("20241116", "20241130"),
    listOf("20241201", "20241215"), listOf("20241216", "20241231")
)

@Composable
fun PayStubsScreen() {
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    Box(modifier = Modifier.fillMaxSize()) { // Fill the entire screen
        Scaffold(
            topBar = { XmlTopBar(titleText = "Pay Stubs") },
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    BiWeeklyDropDown(biWeeklyDateRanges2024 = biWeeklyDateRanges2024, today = today)
                    // Other content can go here
                }
            }
        )
        PayRequests(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)) // Positioned at the bottom right of the Box
    }
}
@Composable
fun BiWeeklyDropDown(biWeeklyDateRanges2024: List<List<String>>, today: String) {
    var expanded by remember { mutableStateOf(false) }
    val validBiWeeklyDateRanges = biWeeklyDateRanges2024.filter { it[1] < today || it[0] == today }

    // Find the current or nearest future bi-weekly period index
    val currentPeriodIndex = validBiWeeklyDateRanges.indexOfFirst { today >= it[0] && today <= it[1] }
    var selectedIndex by remember { mutableStateOf(if (currentPeriodIndex != -1) currentPeriodIndex else 0) }

    // Prepare to store and display fetched work logs
    var workLogs by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // React to selection changes and fetch data
    LaunchedEffect(selectedIndex) {
        getWorkedHoursForDateRange(validBiWeeklyDateRanges[selectedIndex]) { logs ->
            workLogs = logs
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedRangeText = if (validBiWeeklyDateRanges.isNotEmpty())
                    "${validBiWeeklyDateRanges[selectedIndex][0]} to ${validBiWeeklyDateRanges[selectedIndex][1]}"
                else
                    "Select Date Range"

                Text(
                    text = selectedRangeText,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow"
                )
            }
            if (expanded) {
                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                validBiWeeklyDateRanges.forEachIndexed { index, dateRange ->
                    Text(
                        "${dateRange[0]} to ${dateRange[1]}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedIndex = index
                                expanded = false
                            }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }

    // Display work logs
    DisplayWorkLogs(workLogs = workLogs)
}


private fun getWorkedHoursForDateRange(dateRange: List<String>, onResult: (List<Map<String, Any>>) -> Unit) {
    val db = Firebase.firestore
    val startDate = SimpleDateFormat("yyyyMMdd").parse(dateRange[0])
    val endDate = SimpleDateFormat("yyyyMMdd").parse(dateRange[1])

    db.collection("totalHours")
        .whereEqualTo("bid", GlobalUserData.bid)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val approvedWorkLogs = mutableListOf<Map<String, Any>>()

            querySnapshot.documents.forEach { documentSnapshot ->
                val workLogs = documentSnapshot.data as Map<String, Any>?
                workLogs?.forEach { (key, value) ->
                    if (key != "bid") {
                        val uid = key // Assume the key that is not "bid" is the UID
                        (value as List<Map<String, Any>>).forEach { log ->
                            val approved = log["approved"] as Boolean?
                            if (approved == true) {
                                log.keys.filter { it.matches(Regex("\\d{8}")) }.forEach { dateKey ->
                                    try {
                                        val logDate = SimpleDateFormat("yyyyMMdd").parse(dateKey)
                                        if (logDate in startDate..endDate) {
                                            val hours = log[dateKey] as? Long ?: 0L // Assuming hours are stored as Long
                                            // Create a new map that includes the UID, the date, and the hours
                                            val logWithUid = mapOf("uid" to uid, "date" to dateKey, "hours" to hours)
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
            }

            // Return the result through the callback
            onResult(approvedWorkLogs)
        }
        .addOnFailureListener { e ->
            // Handle the error appropriately, maybe log or show a UI indication
        }
}


@Composable
fun DisplayWorkLogs(workLogs: List<Map<String, Any>>) {
    // Aggregate workLogs by UID, summing up the hours
    val aggregatedLogs = workLogs.groupBy { it["uid"] as? String ?: "" }
        .mapValues { entry ->
            entry.value.sumOf { it["hours"] as? Long ?: 0L }
        }

    // Wrap the card display logic in a LazyColumn for scrollable behavior
    LazyColumn {
        itemsIndexed(aggregatedLogs.entries.toList()) { index, (uid, totalHours) ->
            // Convert milliseconds to hours in 2 decimal places
            val hoursInDouble = milliSecondsToHours(totalHours)

            // State for storing the name and wage fetched asynchronously
            var name by remember { mutableStateOf(uid) } // Initialize with UID as a fallback
            var wage by remember { mutableStateOf(0.0) }
            var pay by remember { mutableStateOf(0.0) }

            // Determine the background color based on the index
            val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)

            // Fetch the user's name based on the UID and calculate pay
            LaunchedEffect(uid) {
                uidToName(uid) { fetchedName ->
                    if (fetchedName.isNotEmpty()) {
                        name = fetchedName // Update the name only if fetched name is not empty
                    }
                }

                getWage(uid) { fetchedWage ->
                    wage = fetchedWage
                    pay = hoursInDouble * wage // Calculate pay
                }
            }

            // UI to display the user's name, the sum of hours, and the pay
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = "$hoursInDouble hrs", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = "$%.2f".format(pay), fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Blue)
                }
            }
        }
    }
}

@Composable
fun PayRequests(modifier: Modifier = Modifier) {
    val buttonColor = androidx.compose.material.MaterialTheme.colors.run { Color(0xFF75f8e2) }
    val payRequestsBtn = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(payRequestsBtn, PayRequestsActivity::class.java)
            payRequestsBtn.startActivity(intent)
        },
        modifier = modifier.zIndex(1f) ,
        colors = ButtonDefaults.run { buttonColors(buttonColor) },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("Pay Requests", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}




fun getWage(uid: String, callback: (Double) -> Unit) {
    val db = Firebase.firestore

    db.collection("users")
        .whereEqualTo("UID", uid)
        .get()
        .addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.forEach { documentSnapshot ->
                val user = documentSnapshot.data
                if (user != null) {
                    val wage = user["wage"] as Double
                    callback(wage)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.w("ExpensesRequestsScreen", "Error getting user name", e)
        }
}



@Preview
@Composable
fun PayStubsScreenPreview() {
    PayStubsScreen()
}