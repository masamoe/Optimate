package com.example.optimate.employeeFlow

import android.os.Bundle
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ViewAllPayStubs : AppCompatActivity() {
    private lateinit var expandableListView: ExpandableListView
    private lateinit var expandableListAdapter: ExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_pay_stubs)
        expandableListView = findViewById(R.id.payStubList)

        // Generate biweekly list starting from the first Friday of the current year
        val biweeklyList = generateBiweeklyList()

        // Filter out future pay periods
        val filteredBiweeklyList = filterBiweeklyList(biweeklyList)

        expandableListAdapter = SimpleExpandableListAdapter(
            this,
            filteredBiweeklyList,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("Group"),
            intArrayOf(android.R.id.text1),
            ArrayList<List<Map<String, String>>>(), // Empty list for child data
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("Child"),
            intArrayOf(android.R.id.text1)
        )

        expandableListView.setAdapter(expandableListAdapter)
    }

    private fun generateBiweeklyList(): List<Map<String, String>> {
        val biweeklyList = ArrayList<Map<String, String>>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1) // Set to the first day of the year
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        for (i in 1..26) { // Assuming 26 biweekly periods in a year
            val startDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 13)
            val endDate = calendar.time

            val biweeklyPeriod = HashMap<String, String>()
            biweeklyPeriod["Group"] = dateFormat.format(startDate) + " - " + dateFormat.format(endDate)
            biweeklyList.add(biweeklyPeriod)

            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next Friday
        }

        return biweeklyList
    }

    private fun filterBiweeklyList(biweeklyList: List<Map<String, String>>): List<Map<String, String>> {
        val filteredList = ArrayList<Map<String, String>>()

        val currentDate = Calendar.getInstance().time

        for (item in biweeklyList) {
            val periodString = item["Group"] ?: continue
            val periodDates = periodString.split(" - ")
            val startDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).parse(periodDates[0]) ?: continue
            val endDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).parse(periodDates[1]) ?: continue

            if (currentDate.after(endDate)) {
                filteredList.add(item)
            }
        }

        return filteredList
    }

}