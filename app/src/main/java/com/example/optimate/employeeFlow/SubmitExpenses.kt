package com.example.optimate.employeeFlow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubmitExpenses : AppCompatActivity() {

    private lateinit var expenseAmountEditText: TextInputEditText
    private lateinit var outlinedExpenseAmountLayout: TextInputLayout
    private lateinit var receiptPhotoEditText: TextInputEditText
    private lateinit var outlinedReceiptPhotoLayout: TextInputLayout
    private lateinit var reasonFieldEditText: TextInputEditText
    private lateinit var outlinedReasonFieldLayout: TextInputLayout
    private lateinit var expenseDateEditText: TextView
    private lateinit var sendButton: MaterialButton
    private lateinit var outlinedExpenseDateLayout: TextInputLayout
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage.reference;
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_expenses)

        sendButton = findViewById(R.id.sendExpenseReq)
        expenseAmountEditText = findViewById(R.id.expenseAmount)
        outlinedExpenseAmountLayout = findViewById(R.id.outlinedExpenseAmount)
        receiptPhotoEditText = findViewById(R.id.receiptPhoto)
        outlinedReceiptPhotoLayout = findViewById(R.id.outlinedReceiptPhoto)
        reasonFieldEditText = findViewById(R.id.reasonField)
        outlinedReasonFieldLayout = findViewById(R.id.outlinedReasonField)
        expenseDateEditText = findViewById(R.id.expenseDate)
        outlinedExpenseDateLayout = findViewById(R.id.outlinedExpenseDate)

        var datetoDb = ""
        var reason = ""
        var amount = ""
        var receiptPhoto = ""


        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Find TextInputLayouts and TextInputEditTexts



        // Set click listeners to open date pickers
        outlinedExpenseDateLayout.setEndIconOnClickListener {
            startDatePicker.show(supportFragmentManager, "START_DATE_PICKER_TAG")
        }
        expenseDateEditText.setOnClickListener {
            startDatePicker.show(supportFragmentManager, "START_DATE_PICKER_TAG")
        }

        startDatePicker.addOnPositiveButtonClickListener { startTimestamp ->
            val startDate = Date(startTimestamp)
            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
            val formattedDate = dateFormat.format(startDate)

            datetoDb = formattedDate
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


        sendButton.setOnClickListener {
            // Get values from input fields
            val datetoDb = expenseDateEditText.text.toString()
            val reason = reasonFieldEditText.text.toString()
            val amount = expenseAmountEditText.text.toString()

            // Check if all fields are filled and an image is selected
            if (datetoDb.isNotEmpty() && reason.isNotEmpty() && amount.isNotEmpty() && selectedImageUri != null) {
                // Upload data to Firestore
                uploadReceiptPhotoToStorage(selectedImageUri!!, datetoDb, reason, amount)
            } else {
                // Show toast message for empty fields
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            selectedImageUri?.let {
                val imageName = getImageName(it)
                receiptPhotoEditText.setText(imageName)
            }
        }
    }

    private fun pickPhoto() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickMediaLauncher.launch(pickPhotoIntent)
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



    private fun uploadReceiptPhotoToStorage(imageUri: Uri, datetoDb: String, reason: String, amount: String) {
        val fileName = getFileName(applicationContext, imageUri)
        val uploadTask = storageRef.child("receiptPhotos/${GlobalUserData.uid}/$fileName").putFile(imageUri)

        // On success, download the file URL and display it
        uploadTask.addOnSuccessListener { _ ->
            // Get download URL
            storageRef.child("receiptPhotos/${GlobalUserData.uid}/$fileName").downloadUrl.addOnSuccessListener { uri ->
                // Save URL and other details to Firestore
                saveExpensesRequestToFirestore(datetoDb, reason, amount, uri.toString())
            }.addOnFailureListener {
                Log.e("Firebase", "Failed to get download URL")
                Toast.makeText(this, "Failed to upload receipt photo", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail")
            Toast.makeText(this, "Failed to upload receipt photo", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
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

    private fun saveExpensesRequestToFirestore(datetoDb: String, reason: String, amount: String, receiptPhoto: String ) {



        val submitExpense = hashMapOf(
            "dateOfRequest" to Date(),
            "uid" to GlobalUserData.uid,
            "bid" to GlobalUserData.bid,
            "name" to GlobalUserData.name,
            "expenseDate" to datetoDb,
            "amount" to amount,
            "status" to "pending",
            "receiptPhoto" to receiptPhoto,
            "reason" to reason
        )

        db.collection("expenseRequest")
            .add(submitExpense)
            .addOnSuccessListener { documentReference ->
                Log.d("EditSubmitRequest", "New record created with ID: ${documentReference.id}")
                Toast.makeText(this, "Your request has been Sent for approval", Toast.LENGTH_SHORT).show()
                // Start PayStub activity
                startActivity(Intent(this, PayStub::class.java))
            }
            .addOnFailureListener { e ->
                Log.e("EditAccountActivity", "Error creating new record", e)
                Toast.makeText(this, "Error in Sending", Toast.LENGTH_SHORT).show()
                // Handle the error, for example, show an error message to the user
            }
    }
}
