package com.example.optimate.businessOwner
//noinspection UsingMaterialAndMaterial3Libraries
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable

fun FinancesScreen() {
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
                ViewMore()

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
fun ViewMore(){
    val viewMoreBtn = LocalContext.current
    val buttonColor = colors.run { Color(0xFF75f8e2) }
    Button(
        onClick = {
            val intent = Intent(viewMoreBtn, FinancesDetailActivity::class.java)
            viewMoreBtn.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(buttonColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("View More", color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}

@Preview
@Composable
fun FinancesScreenPreview() {
    FinancesScreen()
}