package com.example.optimate.employeeFlow

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.MutableLiveData
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.example.optimate.loginAndRegister.biWeeklyDateRanges2024
import com.example.optimate.loginAndRegister.milliSecondsToHours
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*

class ViewAllPayStubs : AppCompatActivity() {
    private val yearlyTotalHour = MutableLiveData<Double>().apply { value = 0.0 }
    private val yearlyIncome = MutableLiveData<Double>().apply { value = 0.0 }
    private val yearlyWorkLogs = mutableStateListOf<Map<String, Any>>()
    private lateinit var validBiWeeklyDateRanges: List<List<String>>
    private val payStubsData = mutableStateListOf<Map<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_pay_stubs)
        getWorkedHoursForDateRange(listOf("20240101", "20241231"), yearlyWorkLogs,yearlyTotalHour, yearlyIncome)


        val totalGrossPayAnnual = findViewById<TextView>(R.id.totalGrossPayAnnual)
        val totalNetPayAnnual = findViewById<TextView>(R.id.netPayTotalAnnual)
        val totalTaxesAnnual = findViewById<TextView>(R.id.taxesTotalAnnual)

        yearlyIncome.observe(this) { income ->
            totalGrossPayAnnual.text = "$${income}"
            totalNetPayAnnual.text = "$${income?.times(0.8)}"
            totalTaxesAnnual.text = "$${income?.times(0.2)}"
        }

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val todaysDate: TextView = findViewById(R.id.todaysDate)
        todaysDate.text = "As of $formattedDate:"

        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        validBiWeeklyDateRanges = biWeeklyDateRanges2024.filter { it[0] <= today }

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            ViewAllPayStubsList(validBiWeeklyDateRanges)
        }

//        generatePayStubsDataFromRanges(validBiWeeklyDateRanges, payStubsData) {
//            // This code block is executed after generatePayStubsDataFromRanges is complete
//            val myAdapter = MyAdapter(this, payStubsData)
//            val payStubList: ListView = findViewById(R.id.payStubList)
//            payStubList.adapter = myAdapter
//        }
    }
//    private fun generatePayStubsDataFromRanges(ranges: List<List<String>>,
//                                               payStubsData: MutableList<Map<String, String>>,
//                                               onCompleted: () -> Unit){
//        val inputDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//        val outputDateFormat = SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault())
//
//        for (range in ranges) {
//            if (range.size >= 2) { // Ensure that the range has at least two elements
//                val startDate = inputDateFormat.parse(range[0])
//                val endDate = inputDateFormat.parse(range[1])
//
//                val payStub = HashMap<String, String>()
//                payStub["Date"] =
//                    outputDateFormat.format(startDate) + " to " + outputDateFormat.format(endDate)
//                payStub["TotalHours"] = ""
//                payStub["GrossPay"] = ""
//                payStub["NetPay"] = ""
//                payStub["Taxes"] = ""
//                payStubsData.add(payStub)
//                Log.d("PayStubs1", "Pay stub: $payStub")
//            } else {
//                Log.e("PayStubs", "Invalid date range: $range")
//            }
//        }
//        onCompleted()
//    }
//    private class MyAdapter(
//        private val context: Context,
//        private val data: MutableList<Map<String, String>>
//    ) : BaseAdapter() {
//        private var expandedPosition = -1
//
//        override fun getCount(): Int {
//            return data.size
//        }
//
//        override fun getItem(position: Int): Any {
//            return data[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            val view: View = convertView ?: LayoutInflater.from(context)
//                .inflate(R.layout.pay_stub_sub_list, parent, false)
//
//            val payStubDateTextView: TextView = view.findViewById(R.id.payStubDate)
//            val totalHoursTextView: TextView = view.findViewById(R.id.totalHoursPerPayStub)
//            val grossPayTextView: TextView = view.findViewById(R.id.grossPayPerPayStub)
//            val netPayTextView: TextView = view.findViewById(R.id.netPayPerPayStub)
//            val taxesTextView: TextView = view.findViewById(R.id.taxesPerPayStub)
//            val arrowIcon: ImageView = view.findViewById(R.id.arrowIcon)
//            val payStubChildItem: LinearLayout = view.findViewById(R.id.payStubChildItem)
//
//
//            val item: Map<String, String> = getItem(position) as Map<String, String>
//            payStubDateTextView.text = item["Date"]
//            totalHoursTextView.text = item["TotalHours"]
//            grossPayTextView.text = item["GrossPay"]
//            netPayTextView.text = item["NetPay"]
//            taxesTextView.text = item["Taxes"]
//
//            payStubChildItem.visibility =
//                if (expandedPosition == position) View.VISIBLE else View.GONE
//
//            // Change arrow image based on visibility of child item
//            if (expandedPosition == position) {
//                arrowIcon.setImageResource(R.drawable.arrow_drop_up)
//            } else {
//                arrowIcon.setImageResource(R.drawable.arrow_drop_down)
//            }
//
//            // Set onClickListener to toggle visibility of child item and change arrow image
//            view.setOnClickListener {
//                expandedPosition = if (expandedPosition == position) -1 else position
//                notifyDataSetChanged()
//            }
//
//            return view
//        }
//    }

    private fun getWorkedHoursForDateRange(
        dateRange: List<String>,
        approvedWorkLogs: MutableList<Map<String, Any>>,
        hours: MutableLiveData<Double>,
        income: MutableLiveData<Double>,
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
                                            val hours = log[dateKey] as? Long
                                                ?: 0L // Assuming hours are stored as Long
                                            val wage = (log["wage"] as? Number)?.toDouble() ?: 0.0
                                            // Create a new map that includes the UID, the date, and the hours
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
                //call countTotalHoursAndIncome to calculate the total hours and income
                countTotalHoursAndIncome(approvedWorkLogs, hours, income)

            }
            .addOnFailureListener { e ->
                Log.e("ViewAllPayStubs", "Error getting documents: ", e)
            }
    }

    private fun countTotalHoursAndIncome(
        workLogs: List<Map<String, Any>>,
        hours: MutableLiveData<Double>,
        income: MutableLiveData<Double>
    ) {
        var totalHours = 0.0
        var totalIncome = 0.0
        workLogs.forEach { log ->
            val totalHour = log["hours"] as Long
            val wage = log["wage"] as Double
            val theIncome = milliSecondsToHours(totalHour) * wage
            totalHours += milliSecondsToHours(totalHour)
            totalIncome += theIncome

        }
        hours.postValue(String.format("%.2f", totalHours).toDouble())
        income.postValue(String.format("%.2f", totalIncome).toDouble())
    }
}
