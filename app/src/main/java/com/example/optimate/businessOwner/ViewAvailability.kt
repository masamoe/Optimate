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
    private val adapter = CalendarAdapter()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_availability)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val ivCalendarNext = findViewById<ImageView>(R.id.iv_calendar_next)
        val ivCalendarPrevious = findViewById<ImageView>(R.id.iv_calendar_previous)



        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
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
        currentUser?.let { fetchEmployeeNames("d89RXe3xFjNNCEAftuslt3pGWR23ab") }



    }

    private fun fetchEmployeeNames(businessId: String) {
        val db = FirebaseFirestore.getInstance()
        val employeeAvailabilityLayout = findViewById<LinearLayout>(R.id.employeeAvailabilityLayout)

        // Fetch all users with the provided BID
        db.collection("users")
            .whereEqualTo("BID", businessId)
            .get()
            .addOnSuccessListener { documents ->
                val inflater = LayoutInflater.from(this@ViewAvailability)
                for (document in documents) {
                    val name = document.getString("name")
                    name?.let {
                        // Inflate the employee_availability_card.xml layout
                        val cardView = inflater.inflate(R.layout.employee_availability_card, null) as LinearLayout
                        val employeeNameTextView = cardView.findViewById<TextView>(R.id.employeeName1)
                        employeeNameTextView.text = it
                        // Add the card view to the LinearLayout
                        employeeAvailabilityLayout.addView(cardView)

                        // Set click listener for each day card
                        cardView.setOnClickListener { view ->
                            val availability = document.get("availability") as Map<String, Any>
                            val dayOfWeek = view.tag as String
                            val availabilityForDay = availability[dayOfWeek] as List<String>
                            updateToggleButtons(availabilityForDay)
                        }
                    }
                }
                Log.d("ViewAvailability", "Fetched employee names and added cards dynamically")
            }
            .addOnFailureListener { exception ->
                Log.e("ViewAvailability", "Error getting employee names: ", exception)
            }
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

    class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {
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
            val cardView = holder.itemView.findViewById<MaterialCardView>(R.id.card_calendar)

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
            cardView.tag = SimpleDateFormat("EEEE", Locale.ENGLISH).format(list[position].data)

            // Enable clicking on the cardView
            cardView.isClickable = true
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



    private fun updateToggleButtons(availabilityForDay: List<String>) {
        val morningButton = findViewById<MaterialButton>(R.id.morning)
        val eveningButton = findViewById<MaterialButton>(R.id.evening)
        val anyButton = findViewById<MaterialButton>(R.id.anyT)

        // Clear existing selection
        morningButton.isChecked = false
        eveningButton.isChecked = false
        anyButton.isChecked = false

        // Update toggle buttons according to availability for the day
        availabilityForDay.forEach {
            when (it) {
                "MORNING" -> morningButton.isChecked = true
                "EVENING" -> eveningButton.isChecked = true
                "ANY" -> anyButton.isChecked = true
            }
        }
    }
}



