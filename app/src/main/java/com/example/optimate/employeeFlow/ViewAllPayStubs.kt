package com.example.optimate.employeeFlow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import java.text.SimpleDateFormat
import java.util.*

class ViewAllPayStubs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_pay_stubs)

        // Sample data for demonstration
        val payStubsData = generateBiweeklyDates()

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val todaysDate: TextView = findViewById(R.id.todaysDate)
        todaysDate.text = "As of $formattedDate:"

        val myAdapter = MyAdapter(this, payStubsData)
        val payStubList: ListView = findViewById(R.id.payStubList)
        payStubList.adapter = myAdapter
    }

    private fun generateBiweeklyDates(): ArrayList<Map<String, String>> {
        val payStubsData = ArrayList<Map<String, String>>()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val firstDayOfYear = Calendar.getInstance()
        firstDayOfYear.set(Calendar.YEAR, year)
        firstDayOfYear.set(Calendar.MONTH, Calendar.JANUARY)
        firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1)
        var currentDayOfYear = firstDayOfYear.get(Calendar.DAY_OF_YEAR)

        // Find the first Friday of the year
        while (firstDayOfYear.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            firstDayOfYear.add(Calendar.DAY_OF_YEAR, 1)
            currentDayOfYear++
        }

        val dateFormat = SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault())

        // Generate biweekly dates
        while (currentDayOfYear <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) {
            val payStub = HashMap<String, String>()
            val startDate = firstDayOfYear.time
            val endDate = Calendar.getInstance()
            endDate.time = startDate
            endDate.add(Calendar.DAY_OF_YEAR, 13) // Add 13 days for a two-week period
            payStub["Date"] = dateFormat.format(startDate) + " to " + dateFormat.format(endDate.time)
            payStubsData.add(payStub)

            firstDayOfYear.add(Calendar.DAY_OF_YEAR, 14) // Move to next two-week period
            currentDayOfYear += 14
        }

        return payStubsData
    }


    private class MyAdapter(
        private val context: Context,
        private val data: ArrayList<Map<String, String>>
    ) : BaseAdapter() {
        private var expandedPosition = -1

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.pay_stub_sub_list, parent, false)

            val payStubDateTextView: TextView = view.findViewById(R.id.payStubDate)
            val totalHoursTextView: TextView = view.findViewById(R.id.totalHoursPerPayStub)
            val overTimeTextView: TextView = view.findViewById(R.id.overTimePerPayStub)
            val grossPayTextView: TextView = view.findViewById(R.id.grossPayPerPayStub)
            val netPayTextView: TextView = view.findViewById(R.id.netPayPerPayStub)
            val taxesTextView: TextView = view.findViewById(R.id.taxesPerPayStub)
            val arrowIcon: ImageView = view.findViewById(R.id.arrowIcon)
            val payStubChildItem: LinearLayout = view.findViewById(R.id.payStubChildItem)

            val item: Map<String, String> = getItem(position) as Map<String, String>
            payStubDateTextView.text = item["Date"]
            totalHoursTextView.text = item["TotalHours"]
            overTimeTextView.text = item["Overtime"]
            grossPayTextView.text = item["GrossPay"]
            netPayTextView.text = item["NetPay"]
            taxesTextView.text = item["Taxes"]

            payStubChildItem.visibility = if (expandedPosition == position) View.VISIBLE else View.GONE

            // Change arrow image based on visibility of child item
            if (expandedPosition == position) {
                arrowIcon.setImageResource(R.drawable.arrow_drop_up)
            } else {
                arrowIcon.setImageResource(R.drawable.arrow_drop_down)
            }

            // Set onClickListener to toggle visibility of child item and change arrow image
            view.setOnClickListener {
                expandedPosition = if (expandedPosition == position) -1 else position
                notifyDataSetChanged()
            }

            return view
        }
    }
}
