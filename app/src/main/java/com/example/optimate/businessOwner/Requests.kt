package com.example.optimate.businessOwner


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.optimate.R
import com.example.optimate.employeeFlow.TimeRequest
import com.example.optimate.employeeFlow.TimeRequestStatus
import com.example.optimate.loginAndRegister.DynamicLandingActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Requests : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserBid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, DynamicLandingActivity::class.java)
            startActivity(intent)
        }

        val view_archive = findViewById<Button>(R.id.view_archive_btn)
        view_archive.setOnClickListener {
            // Create an Intent to start the ViewArchive activity
            val intent = Intent(this, ViewArchive::class.java)
            startActivity(intent)
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Check if user is authenticated
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is authenticated, fetch BID from Firestore
            fetchBid(currentUser.uid)
        } else {
            // User is not authenticated, handle accordingly
            // For example, redirect to login screen
            Log.d(TAG, "onCreate: User not authenticated, redirecting to login screen")
        }

    }

    private fun fetchBid(uid: String) {
        Log.d(TAG, "fetchBid: Fetching BID for UID: $uid")

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").whereEqualTo("UID", uid)

        userRef.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents.first() // Get the first document
                    Log.d(TAG, "fetchBid: Document data: ${document.data}")
                    val bid = document.getString("BID")
                    if (bid != null) {
                        currentUserBid = bid
                        Log.d(TAG, "fetchBid: BID fetched successfully: $currentUserBid")
                        // Now you can use currentUserBid in other functions
                        // Call any function that requires currentUserBid here
                        fetchTimeOffRequests()
                    } else {
                        // Handle case where BID is not found in the document
                        Log.e(TAG, "fetchBid: BID not found in document")
                    }
                } else {
                    // Handle case where no documents match the query
                    Log.e(TAG, "fetchBid: No documents found for UID: $uid")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "fetchBid: Error fetching documents", exception)
                // Handle errors
            }

            .addOnFailureListener { exception ->
                Log.e(TAG, "fetchBid: Error fetching document", exception)
                // Handle errors
            }
    }

    companion object {
        const val TAG = "Requests"
    }

    private fun fetchTimeOffRequests() {
        // Assuming timeOffRequests is a collection reference in Firestore
        firestore.collection("timeOffRequest")
            .whereEqualTo("bid", currentUserBid)
            .whereEqualTo("status", TimeRequestStatus.PENDING)

            .get()
            .addOnSuccessListener { documents ->
                val timeRequests = mutableListOf<TimeRequest>()
                for (document in documents) {
                    // Convert Firestore document to TimeRequest object
                    val timeRequest = document.toObject(TimeRequest::class.java)
                    timeRequests.add(timeRequest)
                }
                displayTimeOffRequests(timeRequests)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e(
                    "ViewTimeOffRequests",
                    "Failed to fetch time off requests: ${exception.message}"
                )
            }
    }

    private fun displayTimeOffRequests(timeRequests: List<TimeRequest>) {
        Log.d("ViewTimeOffRequests", "Displaying time off requests. Count: ${timeRequests.size}")



        val requestsLayout = findViewById<LinearLayout>(R.id.RequestsLayout)
        requestsLayout.removeAllViews() // Clear previous views

        if (timeRequests.isEmpty()) {
            Log.d("ViewTimeOffRequests", "No time off requests found.")
            val placeHolderView = findViewById<View>(R.id.placeHolder)
            placeHolderView?.visibility = View.VISIBLE
        } else {
            Log.d("ViewTimeOffRequests", "Time off requests found. Count: ${timeRequests.size}")
            val placeHolderView = findViewById<View>(R.id.placeHolder)
            placeHolderView?.visibility = View.GONE

            timeRequests.forEach { timeRequest ->
                // Inflate the time_request_card layout
                val timeRequestCard = layoutInflater.inflate(R.layout.requests_card, null)

                // Set values to the views inside the card
                timeRequestCard.findViewById<TextView>(R.id.empName).text = timeRequest.name
                timeRequestCard.findViewById<TextView>(R.id.requestStartDate).text =
                    timeRequest.startDate
                timeRequestCard.findViewById<TextView>(R.id.requestEndDate).text = timeRequest.endDate
                timeRequestCard.findViewById<TextView>(R.id.reqStatus).text =
                    "Time request from ${timeRequest.startTime} to ${timeRequest.endTime} is ${timeRequest.status}"

                timeRequestCard.findViewById<Button>(R.id.decline).setOnClickListener {
                    declineTimeOffRequest(timeRequest)
                }

                timeRequestCard.findViewById<Button>(R.id.approve).setOnClickListener {
                    approveTimeOffRequest(timeRequest)
                }

                // Set card background color based on status
                val cardView = timeRequestCard.findViewById<MaterialCardView>(R.id.requestCard)
                val backgroundColor = when (timeRequest.status) {
                    TimeRequestStatus.PENDING -> ContextCompat.getColor(this, R.color.light_yellow)
                    else -> Color.TRANSPARENT
                }
                cardView.setCardBackgroundColor(backgroundColor)


                // Add the inflated card to the layout
                requestsLayout.addView(timeRequestCard)
            }
        }
    }
    private fun declineTimeOffRequest(timeRequest: TimeRequest) {
        // Assuming 'dateOfRequest' is the name of the field storing the timestamp in Firestore
        val dateOfRequest = timeRequest.dateOfRequest

        firestore.collection("timeOffRequest")
            .whereEqualTo("dateOfRequest", dateOfRequest)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Assuming there's only one document matching the dateOfRequest
                    val documentSnapshot = documents.documents[0]
                    val requestId = documentSnapshot.id

                    // Update the status of the found document to 'CANCELLED'
                    firestore.collection("timeOffRequest")
                        .document(requestId)
                        .update("status", TimeRequestStatus.REJECTED)
                        .addOnSuccessListener {
                            Log.d("ViewTimeOffRequests", "Time off request cancelled successfully.")
                            // After cancelling, fetch and display the updated list of time off requests
                            fetchTimeOffRequests()
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "ViewTimeOffRequests",
                                "Failed to cancel time off request: ${exception.message}"
                            )
                            // Handle failure
                        }
                } else {
                    // No document found matching the specified dateOfRequest
                    Log.e(
                        "ViewTimeOffRequests",
                        "No document found matching dateOfRequest: $dateOfRequest"
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "ViewTimeOffRequests",
                    "Failed to query time off requests: ${exception.message}"
                )
                // Handle failure
            }
    }

    private fun approveTimeOffRequest(timeRequest: TimeRequest) {
        // Assuming 'dateOfRequest' is the name of the field storing the timestamp in Firestore
        val dateOfRequest = timeRequest.dateOfRequest

        firestore.collection("timeOffRequest")
            .whereEqualTo("dateOfRequest", dateOfRequest)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Assuming there's only one document matching the dateOfRequest
                    val documentSnapshot = documents.documents[0]
                    val requestId = documentSnapshot.id

                    // Update the status of the found document to 'CANCELLED'
                    firestore.collection("timeOffRequest")
                        .document(requestId)
                        .update("status", TimeRequestStatus.APPROVED)
                        .addOnSuccessListener {
                            Log.d("ViewTimeOffRequests", "Time off request cancelled successfully.")
                            // After cancelling, fetch and display the updated list of time off requests
                            fetchTimeOffRequests()
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "ViewTimeOffRequests",
                                "Failed to cancel time off request: ${exception.message}"
                            )
                            // Handle failure
                        }
                } else {
                    // No document found matching the specified dateOfRequest
                    Log.e(
                        "ViewTimeOffRequests",
                        "No document found matching dateOfRequest: $dateOfRequest"
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "ViewTimeOffRequests",
                    "Failed to query time off requests: ${exception.message}"
                )
                // Handle failure
            }
    }
}
