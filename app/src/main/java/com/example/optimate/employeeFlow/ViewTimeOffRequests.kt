package com.example.optimate.employeeFlow

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.optimate.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class TimeRequestStatus(val status: String, val date: String) {
    companion object {
        const val PENDING = "pending"
        const val APPROVED = "approved"
        const val REJECTED = "rejected"
    }
}

class ViewTimeOffRequests : AppCompatActivity() {

    // Sample time requests
    private val timeRequests = listOf(
        TimeRequestStatus(TimeRequestStatus.PENDING, "2024-02-01"),
        TimeRequestStatus(TimeRequestStatus.APPROVED, "2024-01-15"),
        TimeRequestStatus(TimeRequestStatus.REJECTED, "2024-01-10"),
        TimeRequestStatus(TimeRequestStatus.PENDING, "2024-01-05"),
        TimeRequestStatus(TimeRequestStatus.APPROVED, "2023-12-20"),
        TimeRequestStatus(TimeRequestStatus.REJECTED, "2023-12-15"),
        TimeRequestStatus(TimeRequestStatus.PENDING, "2023-12-10"),
        TimeRequestStatus(TimeRequestStatus.APPROVED, "2023-12-05"),
        TimeRequestStatus(TimeRequestStatus.REJECTED, "2023-12-01"),
        TimeRequestStatus(TimeRequestStatus.PENDING, "2023-11-25")
    )
    //private val timeRequests = emptyList<TimeRequestStatus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_time_off_requests)

        // Sort timeRequests in descending order based on date
        val sortedTimeRequests = timeRequests.sortedByDescending { it.date }

        // Check if there are any time requests
        if (sortedTimeRequests.isEmpty()) {
            // Hide timeRequestCard and show placeHolder
            findViewById<View>(R.id.placeHolder).visibility = View.VISIBLE
        } else {
            // Hide placeHolder and show timeRequestCard for each time request
            findViewById<View>(R.id.placeHolder).visibility = View.GONE

            val timeRequestsLayout = findViewById<LinearLayout>(R.id.timeRequestsLayout)
            sortedTimeRequests.forEach { timeRequest ->
                // Inflate the correct layout resource for each timeRequestCard
                val timeRequestCard = layoutInflater.inflate(R.layout.time_request_card, null)
                timeRequestCard.findViewById<TextView>(R.id.date).text = timeRequest.date
                timeRequestCard.findViewById<TextView>(R.id.requestStatus).text =
                    "Your time request is ${timeRequest.status}"
                // Determine background tint color based on status
                val cardView = timeRequestCard.findViewById<MaterialCardView>(R.id.timeRequestCard)
                val backgroundColor = when (timeRequest.status) {
                    TimeRequestStatus.PENDING -> ContextCompat.getColor(this, R.color.light_yellow)
                    TimeRequestStatus.APPROVED -> {
                        // If status is approved, hide the edit button and change text of cancel button
                        timeRequestCard.findViewById<View>(R.id.edit).visibility = View.GONE
                        timeRequestCard.findViewById<MaterialButton>(R.id.cancel).text = "Request a cancellation"
                        ContextCompat.getColor(this, R.color.light_green)
                    }
                    TimeRequestStatus.REJECTED -> {
                        // If status is rejected, hide the buttons
                        timeRequestCard.findViewById<View>(R.id.edit).visibility = View.GONE
                        timeRequestCard.findViewById<View>(R.id.cancel).visibility = View.GONE
                        ContextCompat.getColor(this, R.color.light_red)
                    }
                    else -> Color.TRANSPARENT
                }
                cardView.setCardBackgroundColor(backgroundColor)

                timeRequestsLayout.addView(timeRequestCard)
            }
        }
    }


}
