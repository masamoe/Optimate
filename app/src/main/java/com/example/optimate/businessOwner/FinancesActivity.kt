package com.example.optimate.businessOwner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FinancesActivity : AppCompatActivity() {

    data class Amount(val type: String, val amount: String)

    private val db = Firebase.firestore
    private val bid = GlobalUserData.bid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use remember with mutableStateOf to initialize the state
            val monthlyRevenues = remember { mutableDoubleStateOf(0.0) }
            val monthlyExpenses = remember { mutableDoubleStateOf(0.0) }
            val yearlyRevenues = remember { mutableDoubleStateOf(0.0) }
            val yearlyExpenses = remember { mutableDoubleStateOf(0.0) }

            // Update the UI with the latest values
            FinancesScreen(
                monthlyRevenues = monthlyRevenues.doubleValue,
                monthlyExpenses = monthlyExpenses.doubleValue,
                yearlyRevenues = yearlyRevenues.doubleValue,
                yearlyExpenses = yearlyExpenses.doubleValue
            )
        }
    }

    override fun onResume() {
        super.onResume()
        fetchFinances()
    }

    private fun fetchFinances() {
        val month = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        db.collection("finances")
            .whereEqualTo("BID", bid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val monthList = mutableListOf<Amount>()
                val yearList = mutableListOf<Amount>()

                querySnapshot.documents.forEach { documentSnapshot ->
                    listOf("Revenues", "Expenses").forEach { financesType ->
                        val finances = documentSnapshot.get(financesType)
                        if (finances is List<*>) {
                            finances.forEach { finance ->
                                if (finance is Map<*, *> && finance["Approval"] as? Boolean == true) {
                                    val amount = (finance["Amount"] as? Number)?.toString() ?: "0"
                                    val timestamp = finance["Date"] as? com.google.firebase.Timestamp
                                    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).apply {
                                        timeZone = TimeZone.getDefault()
                                    }
                                    val date = timestamp?.toDate()?.let { sdf.format(it) } ?: "Unknown"
                                    val amounts = Amount(financesType, amount)
                                    if (date.substring(0, 2) == month) monthList.add(amounts)
                                    if (date.substring(6, 10) == year) yearList.add(amounts)
                                }
                            }
                        }
                    }
                }

                val totalExpensesInMonth = monthList.filter { it.type == "Expenses" }.sumOf { it.amount.toDouble() }
                val totalRevenuesInMonth = monthList.filter { it.type == "Revenues" }.sumOf { it.amount.toDouble() }
                val totalExpensesInYear = yearList.filter { it.type == "Expenses" }.sumOf { it.amount.toDouble() }
                val totalRevenuesInYear = yearList.filter { it.type == "Revenues" }.sumOf { it.amount.toDouble() }

                // Update your UI here with the fetched data
                updateUI(totalRevenuesInMonth, totalExpensesInMonth, totalRevenuesInYear, totalExpensesInYear)
            }
    }

    private fun updateUI(monthlyRevenues: Double, monthlyExpenses: Double, yearlyRevenues: Double, yearlyExpenses: Double) {
        // You need to use the main thread to update the UI
        runOnUiThread {
            setContent {
                FinancesScreen(
                    monthlyRevenues = monthlyRevenues,
                    monthlyExpenses = monthlyExpenses,
                    yearlyRevenues = yearlyRevenues,
                    yearlyExpenses = yearlyExpenses
                )
            }
        }
    }
}
