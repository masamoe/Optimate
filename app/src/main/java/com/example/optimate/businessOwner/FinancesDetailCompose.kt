package com.example.optimate.businessOwner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class Finances(val type: String, val amount: String, val date: String, val description: String, val name: String)
@Composable
fun FinancesDetailScreen(finances: List<Finances>) {
    Scaffold(
        topBar = { XmlTopBar(titleText = "Finances Details") },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                FinancesList(finances = finances)

            }
        }
    )
}

@Composable
fun FinancesList(finances: List<Finances>) {
    LazyColumn {
        itemsIndexed(finances) { _, finance ->
            FinancesRow(finance)
        }
    }
}

@Composable
fun FinancesRow(finance: Finances) {
    var isExpanded by remember { mutableStateOf(false) }

    // Decide the card color based on the type
    val revenueColor = colors.run { Color(0xFFC4F0E6) }
    val expenseColor = colors.run { Color(0xFFFFDFE7)}
    val cardColor = if (finance.type == "Expenses") expenseColor else revenueColor

    Card(
        backgroundColor = cardColor,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded } // Toggle isExpanded on click
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = finance.amount, style = MaterialTheme.typography.bodyLarge)
            Text(text = finance.date, style = MaterialTheme.typography.bodySmall)
            // Expandable content
            if (isExpanded) {
                Text(text = finance.description, style = MaterialTheme.typography.bodySmall)
                Text(text = finance.name, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}




@Preview
@Composable
fun FinancesDetailScreenPreview() {
    FinancesDetailScreen(
        finances = listOf(
            Finances("Revenues", "100", "2022-10-10", "This is a revenue", "John Doe"),
            Finances("Expenses", "50", "2022-10-10", "This is an expense", "John Doe"),
            Finances("Revenues", "100", "2022-10-10", "This is a revenue", "John Doe"),
            Finances("Expenses", "50", "2022-10-10", "This is an expense", "John Doe"),
            Finances("Revenues", "100", "2022-10-10", "This is a revenue", "John Doe"),
            Finances("Expenses", "50", "2022-10-10", "This is an expense", "John Doe"),
            Finances("Revenues", "100", "2022-10-10", "This is a revenue", "John Doe"),
        )
    )
}