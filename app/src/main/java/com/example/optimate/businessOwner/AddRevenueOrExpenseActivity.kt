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
import com.example.optimate.loginAndRegister.DynamicLandingActivity
import com.example.optimate.loginAndRegister.GlobalUserData
import com.example.optimate.loginAndRegister.addRevenueOrExpenseToDB
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
            val intent = Intent(this, DynamicLandingActivity::class.java)
            startActivity(intent)
            finish()
        }
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            val date = date.text.toString()
            Log.d("AddRevenueOrExpenseActivity", "date: $date")
            val amount = amount.text.toString().toDouble()
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
                calendar.set(Calendar.HOUR_OF_DAY, 12)
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
        val sdf = SimpleDateFormat("MM/dd/yy", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        date.setText(sdf.format(calendar.time))
    }





}
