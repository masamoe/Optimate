import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Shift(val id: Int, val day: String, val employees: List<String>)

val shiftsData = listOf(
    Shift(1, "Monday", listOf("Alice", "Bob")),
    Shift(2, "Tuesday", listOf("Charlie", "David")),
    // Add more shifts as needed
)

@Composable
fun ScheduleManagerPage() {
    val shiftsData by remember { mutableStateOf(shiftsData) }
    var selectedPage by remember { mutableStateOf(0) }

    // Use a ViewPager to enable swiping between days
    ViewPager(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        count = shiftsData.size,
        currentPage = selectedPage,
        onPageChanged = { newPage ->
            selectedPage = newPage
        }
    ) { page ->
        ShiftsPage(shifts = shiftsData[page])
    }
}

@Composable
fun ShiftsPage(shifts: Shift) {
    LazyColumn {
        items(1) {
            ShiftCard(shift = shifts)
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

@Composable
fun ViewPager(
    modifier: Modifier = Modifier,
    count: Int,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    content: @Composable (page: Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
    ) {
        items(count) { page ->
            content(page)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleManagerPagePreview() {
    ScheduleManagerPage()
}