package com.example.optimate.businessOwner
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddRevenueOrExpenseActivity: AppCompatActivity() {

    val db = Firebase.firestore
    val auth = Firebase.auth
    private lateinit var date: TextInputEditText
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var amount: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var type: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_revenue_or_expense)

        val title = findViewById<TextView>(R.id.topBarTitle)
        type = intent.getStringExtra("type").toString()
        title.text = "Add $type"

        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, BusinessLanding::class.java)
            startActivity(intent)
            finish()
        }
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            val date = date.text.toString()
            val amount = amount.text.toString().toFloat()
            val description = description.text.toString()
            addRevenueOrExpenseToDB(type, date, amount, description)
            val intent = Intent(this, FinancesActivity::class.java)
            startActivity(intent)
            finish()
        }

        date = findViewById(R.id.date)
        dateInputLayout = findViewById(R.id.dateInputLayout)
        amount = findViewById(R.id.amount)
        description = findViewById(R.id.description)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Vancouver"), Locale.CANADA)

        // DatePicker dialog
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel(calendar)
            }

        // Click listener for the end icon
        dateInputLayout.setEndIconOnClickListener {
            DatePickerDialog(
                this@AddRevenueOrExpenseActivity, dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()

        }
    }
    private fun updateLabel(calendar: Calendar) {
        val myFormat = "MM/dd/yy" // Your desired format
        val sdf = SimpleDateFormat(myFormat, Locale.CANADA)
        date.setText(sdf.format(calendar.time))
    }

    private fun addRevenueOrExpenseToDB(type: String, dateStr: String, amount: Float, description: String) {
        val bid = GlobalUserData.bid

        // Convert the String date to a Date object
        val sdf = SimpleDateFormat("MM/dd/yy", Locale.CANADA)
        val date = sdf.parse(dateStr)

        val entry = hashMapOf(
            "UID" to (auth.currentUser?.uid ?: ""),
            "Date" to date,
            "Amount" to amount,
            "Description" to description,
            "Approval" to true,
            "Name" to GlobalUserData.name,
            "Uploaded Date" to Timestamp(Date())
        )

        // Reference to the document in the finances collection
        val docRef = db.collection("finances").document(bid)

        // Check if the BID exists in the finances collection
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // If the document exists, append the new entry to the appropriate array
                val fieldName = if (type == "Revenue") "Revenues" else "Expenses"
                docRef.update(fieldName, com.google.firebase.firestore.FieldValue.arrayUnion(entry))
                    .addOnSuccessListener {
                        Log.d("AddRevenueOrExpenseActivity", "$type added successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w("AddRevenueOrExpenseActivity", "Error adding $type", e)
                    }
            } else {
                // If the document does not exist, create a new document with the BID and initialize the Revenues and Expenses arrays
                val initData = hashMapOf(
                    "BID" to bid,
                    "Revenues" to if (type == "Revenue") listOf(entry) else emptyList<Any>(),
                    "Expenses" to if (type == "Expense") listOf(entry) else emptyList<Any>()
                )
                docRef.set(initData)
                    .addOnSuccessListener {
                        Log.d("AddRevenueOrExpenseActivity", "New finances document with $type added successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w("AddRevenueOrExpenseActivity", "Error creating new finances document", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w("AddRevenueOrExpenseActivity", "Error checking document existence", e)
        }
    }



}
