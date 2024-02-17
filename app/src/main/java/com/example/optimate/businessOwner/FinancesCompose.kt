package com.example.optimate.businessOwner
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("ConstantLocale")
private val todayMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
@SuppressLint("ConstantLocale")
private val todayYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
private fun monthToEnglish(month: String): String {
    return when (month) {
        "01" -> "January"
        "02" -> "February"
        "03" -> "March"
        "04" -> "April"
        "05" -> "May"
        "06" -> "June"
        "07" -> "July"
        "08" -> "August"
        "09" -> "September"
        "10" -> "October"
        "11" -> "November"
        "12" -> "December"
        else -> "Invalid month"
    }
}
@Composable

fun FinancesScreen(monthlyRevenues: Double, monthlyExpenses: Double, yearlyRevenues: Double, yearlyExpenses: Double) {
    Scaffold(
        topBar = { XmlTopBar(titleText = "Finances") },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {
                    AddRevenue()
                    AddExpense()
                }

                DonutChart(monthlyRevenues, monthlyExpenses)

            }
        }
    )
}

@Composable
fun AddRevenue() {
    val buttonColor = colors.run { Color(0xFFC4F0E6) }
    val addRevenueBtn = LocalContext.current

    // Add Revenue button
    Button(
        onClick = {
            val intent = Intent(addRevenueBtn, AddRevenueOrExpenseActivity::class.java).apply {
                putExtra("type", "Revenue")
            }
            addRevenueBtn.startActivity(intent)
        },
        colors = ButtonDefaults.run { buttonColors(buttonColor) },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("Add Revenue", color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AddExpense() {
    val buttonColor = colors.run { Color(0xFFFFDFE7)}
    val addExpenseBtn = LocalContext.current

    // Add Expense button
    Button(
        onClick = {
            val intent = Intent(addExpenseBtn, AddRevenueOrExpenseActivity::class.java).apply {
                putExtra("type", "Expense")
            }
            addExpenseBtn.startActivity(intent)
        },
        colors = ButtonDefaults.run { buttonColors(buttonColor) },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp)

    ) {
        Text("Add Expense", color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ViewMore(modifier: Modifier = Modifier){
    val viewMoreBtn = LocalContext.current
    val buttonColor = colors.run { Color(0xFF75f8e2) }
    Button(
        onClick = {
            val intent = Intent(viewMoreBtn, FinancesDetailActivity::class.java)
            viewMoreBtn.startActivity(intent)
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(buttonColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("View More", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}


@Composable
fun DonutChart(monthlyRevenues: Double, monthlyExpenses: Double) {
    val total = monthlyRevenues + monthlyExpenses
    val revenueAngle = (monthlyRevenues / total * 360).toFloat()
    val expensesAngle = 360f - revenueAngle
    val revenueColor = Color(0xFFC4F0E6)
    val expenseColor = Color(0xFFFFDFE7)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    )
    {
        Box(modifier = Modifier
            .align(alignment = Alignment.CenterHorizontally))
        {
            val month = monthToEnglish(todayMonth)
            val year = todayYear
            Text("$month $year", color = colors.onSurface, fontSize = 25.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))

        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Revenue: $${String.format("%.2f", monthlyRevenues)}", fontSize = 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(bottom = 8.dp))
                Text("Expenses: $${String.format("%.2f", monthlyExpenses)}", fontSize = 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(bottom = 8.dp))
                Text("Balance: $${String.format("%.2f", monthlyRevenues - monthlyExpenses)}", fontSize = 12.sp, fontWeight = FontWeight.Normal)

            }
            ViewMore(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(5.dp)
                .height(40.dp)

                )

        Column (
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
            ){

                Canvas(modifier = Modifier.size(200.dp)) { // Set this to your desired size
                    val strokeWidth = 50f
                    val radius = size.minDimension / 2 - strokeWidth / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Draw the arc for monthly revenues
                    drawArc(
                        color = revenueColor,
                        startAngle = -90f,
                        sweepAngle = revenueAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth + 20f),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Draw the arc for monthly expenses
                    drawArc(
                        color = expenseColor,
                        startAngle = -90f + revenueAngle,
                        sweepAngle = expensesAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                }

            }

    }
}
}
@Preview
@Composable
fun FinancesScreenPreview() {
    FinancesScreen(monthlyRevenues = 1000.0, monthlyExpenses = 500.0, yearlyRevenues = 12000.0, yearlyExpenses = 6000.0)
}