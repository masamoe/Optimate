package com.example.optimate.businessOwner

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


data class Shift( val day: String, val startTime: String, val endTime: String, val employees: List<String>)

var shiftsData = listOf(
    Shift("2024-2-20", "08:00", "12:00:00", listOf("Alice", "Bob")),
    Shift("2024-2-21", "08:00", "12:00:00", listOf("Alice", "Bob")),
    // Add more shifts as needed
)

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScheduleManagerPage() {
    val pagerState = rememberPagerState()

    Scaffold(
        topBar = { XmlTopBar(titleText = "Scheduler") },
        content = { innerPadding ->
            HorizontalPager(
                state = pagerState,
                count = shiftsData.size,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(innerPadding)
            ) { page ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly

                    ) {
                        ShiftDate(shift = shiftsData[page])
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                            ShiftsPage(shift = shiftsData[page])
                        }
                    }

            }
        }
    )
}

@Composable
fun ShiftsPage(shift: Shift) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        ShiftCard(shift = shift)
    }
}

@Composable
fun ShiftDate(shift: Shift) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        DateCard(shift = shift)
    }
}

@Composable
fun DateCard(shift: Shift) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = shift.day, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ShiftCard(shift: Shift) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = shift.day, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Employees:")
            for (employee in shift.employees) {
                Text(text = "- $employee")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleManagerPagePreview() {
    ScheduleManagerPage()
}