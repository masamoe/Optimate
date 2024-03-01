package com.example.optimate.businessOwner


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
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
import java.lang.String.format

//workLogsWaitForApproval:
// {lV0Hg8ikbRM6GZZN6FjslXki4MD2=[{20240229=4000}],
// JiHljMeEbuO7vEG1aUaKrbLto5t1=[{20240228=20000}],
// KbClYhd5vXhRPf84jbCPr7cAglq1=[{20240229=4000}, {20240228=40000}]}
@Composable
fun PayRequestsScreen(workLogsWaitForApproval: MutableMap<String, List<Map<String, Long>>>) {
    Scaffold(
        topBar = { XmlTopBar( titleText = "Pay Requests" ) },
        content = {innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                WorkingHoursCardList(workLogsWaitForApproval)
            }
        }
    )
}

@Composable
fun WorkingHoursCardList(workLogsWaitForApproval: MutableMap<String, List<Map<String, Long>>>) {
    LazyColumn {
        itemsIndexed(workLogsWaitForApproval.keys.toList()) { index, userId ->
            val logs = workLogsWaitForApproval[userId] ?: emptyList()
            var userName by remember { mutableStateOf("") }

            LaunchedEffect(userId) {
                uidToName(userId) { name ->
                    userName = name
                }
            }

            logs.forEach { log ->
                WorkingHoursCard(index, userId, userName, log)
            }
        }
    }
}

@Composable
fun WorkingHoursCard(index: Int, userId: String, userName: String, log: Map<String, Long>) {
    val date = log.keys.first()
    val hours = log.values.first()
    var isExpanded by remember { mutableStateOf(false) }
    val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)

    // State to hold work logs details for the date
    val workLogsDetails = remember { mutableStateListOf<Map<String, String>>() }

    // Effect to load work logs when card is expanded
    LaunchedEffect(isExpanded, userId, date) {
        if (isExpanded) {
            val db = Firebase.firestore
            db.collection("workLogs")
                .whereEqualTo("uid", userId)
                .get()
                .addOnSuccessListener { documents ->
                    workLogsDetails.clear()
                    for (document in documents) {
                        val data = document.data.toMutableMap()
                        val dateLogs = data[date] as? List<Map<String, String>> ?: emptyList()
                        workLogsDetails.addAll(dateLogs)
                    }
                }
        }
    }

    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$date: ${milliSecondsToHours(hours)} hs",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                if (isExpanded) {
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        workLogsDetails.forEach { detailMap ->
                            detailMap.forEach { (key, value) ->
                                if (key != "uid" && key != "bid") {
                                    Text(
                                        text = "$key: $value",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Blue
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        ApproveBtn(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // The expand/collapse icon
            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clickable { isExpanded = !isExpanded }
            )
        }
    }
}

@Composable
fun ApproveBtn(modifier: Modifier = Modifier) {
    val buttonColor = androidx.compose.material.MaterialTheme.colors.run { Color(0xFF75f8e2) }
    val approveBtn = LocalContext.current
    Button(
        modifier = modifier,
        onClick = {
            val intent = Intent(approveBtn, PayRequestsActivity::class.java)
            approveBtn.startActivity(intent)
        },
        colors = ButtonDefaults.run { buttonColors(buttonColor) },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("Approve", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
    }

}



////getWorkLogsByUidAndDate("lV0Hg8ikbRM6GZZN6FjslXki4MD2", "20240229")
//fun getWorkLogsByUidAndDate(uid: String, date: String) {
//    val db = Firebase.firestore
//    val workLogsList = mutableListOf<Map<String, Any>>()
//    db.collection("workLogs")
//        .whereEqualTo("uid", uid)
//        .get()
//        .addOnSuccessListener { documents ->
//            for (document in documents) {
//                val data = document.data.toMutableMap()
//                data.remove("uid")
//                data.remove("bid")
//                workLogsList.add(data)
//            }
//            Log.d("PayRequestsActivity", "workLogsList: $workLogsList")
//            //workLogsList:
//            // [{20240229=[{clockIn=02/29/2024 09:27:13}, {breakStart=02/29/2024 09:27:15}, {breakEnd=02/29/2024 09:27:18}, {clockOut=02/29/2024 09:27:20}],
//            // 20240227=[{clockIn=02/27/2024 12:04:31}, {breakStart=02/27/2024 12:04:33}, {breakEnd=02/27/2024 12:04:51}, {clockOut=02/27/2024 12:04:56}],
//            // 20240225=[{clockIn=02/25/2024 00:04:58}, {clockOut=02/25/2024 00:05:02}],
//            // 20240224=[{clockIn=02/24/2024 23:24:07}, {breakStart=02/24/2024 23:24:11}, {breakEnd=02/24/2024 23:24:14}, {clockOut=02/24/2024 23:24:15}]}]
//            for (workLog in workLogsList) {
//                val filteredWorkLog = mutableMapOf<String, Any>()
//                for (key in workLog.keys) {
//                    val workLogDate = key
//                    if (workLogDate == date) {
//                        filteredWorkLog[key] = workLog[key]!!
//                    }
//                }
//                Log.d("PayRequestsActivity", "filteredWorkLog: $filteredWorkLog")
//                //filteredWorkLog: {20240229=[{clockIn=02/29/2024 09:27:13}, {breakStart=02/29/2024 09:27:15}, {breakEnd=02/29/2024 09:27:18}, {clockOut=02/29/2024 09:27:20}]}
//            }
//
//        }
//}

@Preview
@Composable
fun ExpensesRequestsScreenPreview() {
    //workLogsWaitForApproval:
// {lV0Hg8ikbRM6GZZN6FjslXki4MD2=[{20240229=4000}],
// JiHljMeEbuO7vEG1aUaKrbLto5t1=[{20240228=20000}],
// KbClYhd5vXhRPf84jbCPr7cAglq1=[{20240229=4000}, {20240228=40000}]}
    val workLogsWaitForApproval = mutableMapOf<String, List<Map<String, Long>>>()
    workLogsWaitForApproval["lV0Hg8ikbRM6GZZN6FjslXki4MD2"] = listOf(mapOf("20240229" to 4000))
    workLogsWaitForApproval["JiHljMeEbuO7vEG1aUaKrbLto5t1"] = listOf(mapOf("20240228" to 20000))
    workLogsWaitForApproval["KbClYhd5vXhRPf84jbCPr7cAglq1"] = listOf(mapOf("20240229" to 4000), mapOf("20240228" to 40000))
    PayRequestsScreen(workLogsWaitForApproval)
}
