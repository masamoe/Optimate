package com.example.optimate.businessOwner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.optimate.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.text.SimpleDateFormat
import java.util.*

class ViewAvailability : AppCompatActivity() {

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private val adapter = CalendarAdapter(emptyMap(), {})





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_availability)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val ivCalendarNext = findViewById<ImageView>(R.id.iv_calendar_next)
        val ivCalendarPrevious = findViewById<ImageView>(R.id.iv_calendar_previous)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // Initialize the adapter with availability and updateAvailabilityTextView
        adapter.availability = availability
        adapter.updateAvailabilityTextView = this::updateAvailabilityTextView
        recyclerView.adapter = adapter

        setUpCalendar()

        ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            setUpCalendar()
        }

        // Fetch employee names for the current user's BID
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { fetchEmployeeAvailability("d89RXe3xFjNNCEAftuslt3pGWR23ab") }
    }


    private fun fetchEmployeeAvailability(businessId: String) {
        val db = FirebaseFirestore.getInstance()
        val employeeAvailabilityLayout = findViewById<LinearLayout>(R.id.employeeAvailabilityLayout)

        // Clear existing views to avoid duplicates
        employeeAvailabilityLayout.removeAllViews()

        // Fetch availability data for the provided BID
        db.collection("availability")
            .whereEqualTo("BID", businessId)
            .get()
            .addOnSuccessListener { documents ->
                val inflater = LayoutInflater.from(this@ViewAvailability)
                for (document in documents) {
                    val name = document.getString("name")
                    val availabilityData = document.data // Retrieve availability data for the employee
                    name?.let {
                        // Inflate the employee_availability_card.xml layout
                        val cardView = inflater.inflate(R.layout.employee_availability_card, null) as LinearLayout
                        val employeeNameTextView = cardView.findViewById<TextView>(R.id.employeeName1)
                        employeeNameTextView.text = it
                        // Add the card view to the LinearLayout
                        employeeAvailabilityLayout.addView(cardView)

                        cardView.setOnClickListener { view ->
                            val dayOfWeek = view.tag as? String
                            // Retrieve availability data for the clicked day of the week and update UI
                            dayOfWeek?.let { availabilityForDay ->
                                // Pass availability data specific to the employee to the update method
                                updateAvailabilityTextView(availabilityData, availabilityForDay)
                            }
                        }
                    }
                }
                Log.d("ViewAvailability", "Fetched employee names and added cards dynamically")
            }
            .addOnFailureListener { exception ->
                Log.e("ViewAvailability", "Error getting employee names: ", exception)
            }
    }

    private fun updateAvailabilityTextView(availabilityData: Map<String, Any>, availabilityForDay: String) {
        val availabilityTextView = findViewById<TextView>(R.id.availability)

        // Map availability status to corresponding strings
        val availabilityMap = mapOf(
            "MORNING" to "Morning",
            "EVENING" to "Evening",
            "ANY" to "Any",
            "NOT_AVAILABLE" to "Not Available"
        )

        // Retrieve availability status for the selected day for the specific employee
        val availabilityForEmployee = availabilityData[availabilityForDay] as? List<String>
        // If availabilityForEmployee is not null and not empty, get the availability status from the first item
        // Otherwise, set availability status as "Unknown"
        val availabilityStatus = availabilityForEmployee?.firstOrNull()?.let { availabilityMap[it] } ?: "Unknown"

        // Set the availability text
        availabilityTextView.text = availabilityStatus
    }





    private fun setUpCalendar() {
        val tvDateMonth = findViewById<TextView>(R.id.tv_date_month)

        val calendarList = ArrayList<CalendarDateModel>()
        tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        adapter.setData(calendarList)
    }

    data class CalendarDateModel(var data: Date)

    class CalendarAdapter(
        var availability: Map<String, Any>,
        var updateAvailabilityTextView: (List<String>) -> Unit
    ) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {
        private val list = ArrayList<CalendarDateModel>()

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val calendarDay = holder.itemView.findViewById<TextView>(R.id.tv_calendar_day)
            val calendarDate = holder.itemView.findViewById<TextView>(R.id.tv_calendar_date)
            val tvToday = holder.itemView.findViewById<TextView>(R.id.tv_today)

            val currentDate = Date() // Get the current date

            fun isSameDay(date1: Date, date2: Date): Boolean {
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
                return sdf.format(date1) == sdf.format(date2)
            }

            // Set text for calendarDay and calendarDate using SimpleDateFormat
            calendarDay.text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(list[position].data)
            calendarDate.text = SimpleDateFormat("d", Locale.ENGLISH).format(list[position].data)

            // Check if the current date matches the date in the CalendarDateModel
            if (isSameDay(list[position].data, currentDate)) {
                // If it's the current date, show the "(Today)" TextView
                tvToday.visibility = View.VISIBLE
            } else {
                // Otherwise, hide the "(Today)" TextView
                tvToday.visibility = View.GONE
            }

            // Set tag for the cardView to the day of the week
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(list[position].data)
            holder.itemView.tag = dayOfWeek

            // Set click listener for the cardView
            holder.itemView.setOnClickListener { view ->
                val dayOfWeek = view.tag as? String
                // Retrieve availability data for the clicked day of the week and update UI
                dayOfWeek?.let { availabilityForDay ->
                    updateAvailabilityTextView(availability[availabilityForDay] as? List<String> ?: emptyList())
                }
            }
        }






        override fun getItemCount(): Int {
            return list.size
        }

        fun setData(calendarList: ArrayList<CalendarDateModel>) {
            list.clear()
            list.addAll(calendarList)
            notifyDataSetChanged()
        }
    }
    private fun updateAvailabilityTextView(availabilityForDay: List<String>) {
        val availabilityTextView = findViewById<TextView>(R.id.availability)

        // Map availability status to corresponding strings
        val availabilityMap = mapOf(
            "MORNING" to "Morning",
            "EVENING" to "Evening",
            "ANY" to "Any",
            "NOT_AVAILABLE" to "Not Available"
        )

        // If availabilityForDay is not empty, get the availability status from the first item
        // Otherwise, set availability status as "Unknown"
        val availabilityStatus = if (availabilityForDay.isNotEmpty()) {
            // Retrieve the availability status from the first item
            val availability = availabilityForDay[0]
            // Map the availability status to the corresponding string
            availabilityMap[availability] ?: "Unknown"
        } else {
            // If availabilityForDay is empty, set availability status as "Unknown"
            "Unknown"
        }

        // Set the availability text
        availabilityTextView.text = availabilityStatus
    }

    companion object {
        // Define availability as a static variable so that it can be accessed from the adapter
        var availability: Map<String, Any> = emptyMap()
    }


}



