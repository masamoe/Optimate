package com.example.optimate.employeeFlow

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.optimate.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat

class SubmitExpenses : AppCompatActivity() {

    private lateinit var expenseAmountEditText: TextInputEditText
    private lateinit var outlinedExpenseAmountLayout: TextInputLayout
    private lateinit var receiptPhotoEditText: TextInputEditText
    private lateinit var outlinedReceiptPhotoLayout: TextInputLayout
    private lateinit var reasonFieldEditText: TextInputEditText
    private lateinit var outlinedReasonFieldLayout: TextInputLayout

    private val currencyFormat = NumberFormat.getCurrencyInstance()

    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val imageName = getImageName(it)
                receiptPhotoEditText.setText(imageName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_expenses)

        expenseAmountEditText = findViewById(R.id.expenseAmount)
        outlinedExpenseAmountLayout = findViewById(R.id.outlinedExpenseAmount)
        receiptPhotoEditText = findViewById(R.id.receiptPhoto)
        outlinedReceiptPhotoLayout = findViewById(R.id.outlinedReceiptPhoto)
        reasonFieldEditText = findViewById(R.id.reasonField)
        outlinedReasonFieldLayout = findViewById(R.id.outlinedReasonField)

        // Create MaterialDatePicker instances for start and end dates
        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Find TextInputLayouts and TextInputEditTexts
        val outlinedExpenseDateLayout = findViewById<TextInputLayout>(R.id.outlinedExpenseDate)
        val expenseDateEditText = findViewById<TextView>(R.id.expenseDate)

        // Set click listeners to open date pickers
        outlinedExpenseDateLayout.setEndIconOnClickListener {
            startDatePicker.show(supportFragmentManager, "START_DATE_PICKER_TAG")
        }
        expenseDateEditText.setOnClickListener {
            startDatePicker.show(supportFragmentManager, "START_DATE_PICKER_TAG")
        }

        startDatePicker.addOnPositiveButtonClickListener { _ ->
            expenseDateEditText.text = startDatePicker.headerText
            outlinedExpenseDateLayout.error = null // Clear any previous error
        }

        // Set onFocusChangeListeners to handle hints
        expenseAmountEditText.onFocusChangeListener = onFocusChangeListener("$0,000.00")
        reasonFieldEditText.onFocusChangeListener = onFocusChangeListener("Write your comment here")

        // Set click listeners for picking a photo
        receiptPhotoEditText.setOnClickListener {
            // Call the method to pick a photo
            pickPhoto()
        }

        outlinedReceiptPhotoLayout.setEndIconOnClickListener {
            // Call the method to pick a photo
            pickPhoto()
        }
    }

    private fun onFocusChangeListener(hint: String): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                (view as? TextInputEditText)?.hint = hint
            } else {
                (view as? TextInputEditText)?.hint = ""
            }
        }
    }

    private fun pickPhoto() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickMediaLauncher.launch(pickPhotoIntent)
    }

    private fun getImageName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        var name = ""
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    name = cursor.getString(displayNameIndex)
                    // Check if the name length exceeds the limit
                    val maxLength = 32 // Adjust the maximum length as needed
                    if (name.length > maxLength) {
                        // Truncate the name
                        name = name.substring(0, maxLength) + "..."
                    }
                }
            }
        }
        return name
    }
}