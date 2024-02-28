package com.example.optimate.employeeFlow

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.optimate.R
import com.example.optimate.loginAndRegister.DynamicLandingActivity
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class EditProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val emailInput = findViewById<TextView>(R.id.emailUpdate)
        val passwordInput = findViewById<TextView>(R.id.passwordUpdate)
        val addressInput = findViewById<TextView>(R.id.addresUpdate)
        val phoneInput = findViewById<TextView>(R.id.phoneUpdate)
        val roleText = findViewById<TextView>(R.id.textView7)
        val nameText = findViewById<TextView>(R.id.textView6)
        emailInput.text = GlobalUserData.email
        passwordInput.text = GlobalUserData.password
        addressInput.text = GlobalUserData.address
        phoneInput.text = GlobalUserData.phone
        roleText.text = GlobalUserData.role
        nameText.text = GlobalUserData.name



        homeBtn.setOnClickListener {
            val intent = Intent(this, DynamicLandingActivity::class.java)
            startActivity(intent)
        }

        saveBtn.setOnClickListener {
            val userDataAuth = HashMap<String, String>()
            if(GlobalUserData.email != emailInput.text.toString()){
                //updateEmail(emailInput.text.toString())
                //userDataAuth["email"] = emailInput.text.toString()
            }
            if (GlobalUserData.password != passwordInput.text.toString()){
                updatePassword(passwordInput.text.toString())
            }
            if (GlobalUserData.address != addressInput.text.toString()){
                userDataAuth["address"] = addressInput.text.toString()
            }
            if (GlobalUserData.phone != phoneInput.text.toString()) {
                userDataAuth["phone"] = phoneInput.text.toString()
            }

            if (userDataAuth.isEmpty()) {
                return@setOnClickListener
            }else{
                updateDB(userDataAuth)
            }
        }

        // Function to save data to local database

    }
   /* private fun updateEmail(email: String) {
        val user = Firebase.auth.currentUser

        user?.updateEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                    // Provide feedback to the user
                    showToast("Email address updated successfully")

                } else {
                    Log.e(TAG, "Failed to update user email", task.exception)
                    // Provide feedback to the user
                    showToast("Failed to update email address. Please try again later.")

                }
            }
    }*/

    private fun updatePassword(newPassword: String) {
        val user = Firebase.auth.currentUser

        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                    // Provide feedback to the user
                    GlobalUserData.password = newPassword
                    showToast("Password updated successfully")
                } else {
                    Log.e(TAG, "Failed to update user password", task.exception)
                    // Provide feedback to the user
                    showToast("Failed to update password. Please try again later.")
                }
            }
    }

    private fun updateDB(updates : HashMap<String, String>) {
        if (GlobalUserData.uid != null) {
            val userRef = db.collection("users").whereEqualTo("UID", GlobalUserData.uid)

            if (updates.isEmpty()) {
                // No fields to update
                return
            }

            userRef.get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // No document found with the specified UID
                        Log.e(
                            "EditAccountActivity",
                            "No document found for UID: ${GlobalUserData.uid}"
                        )
                        showToast("No document found for UID: ${GlobalUserData.uid}")
                        return@addOnSuccessListener
                    }

                    // Assuming there's only one document with the specified UID
                    val userDoc = documents.documents[0]

                    // Update only the specified fields in the document
                    userDoc.reference.update(updates as Map<String, Any>)
                        .addOnSuccessListener {
                            // Update GlobalUserData with the updated fields
                            if (updates.containsKey("email")) {
                                GlobalUserData.email = updates["email"] as String
                            }
                            if (updates.containsKey("address")) {
                                GlobalUserData.address = updates["address"] as String
                            }
                            if (updates.containsKey("phone")) {
                                GlobalUserData.phone = updates["phone"] as String
                            }

                            Log.d("EditAccountActivity", "User document updated successfully")
                            showToast("Updated")
                        }
                        .addOnFailureListener { e ->
                            Log.e("EditAccountActivity", "Error updating user document", e)
                            showToast("Update Failed")
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("EditAccountActivity", "Error fetching user document", e)
                    showToast("Error fetching user document")
                }
        } else {
            Toast.makeText(this, "Error: No User Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}