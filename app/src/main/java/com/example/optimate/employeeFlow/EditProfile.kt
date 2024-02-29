package com.example.optimate.employeeFlow

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.storage

class EditProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }

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
        val iconButton = findViewById<Button>(R.id.iconButton)
        emailInput.text = GlobalUserData.email
        passwordInput.text = GlobalUserData.password
        addressInput.text = GlobalUserData.address
        phoneInput.text = GlobalUserData.phone
        roleText.text = GlobalUserData.role
        nameText.text = GlobalUserData.name

        iconButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }



        homeBtn.setOnClickListener {
            val intent = Intent(this, DynamicLandingActivity::class.java)
            startActivity(intent)
        }

        saveBtn.setOnClickListener {
            val userDataAuth = HashMap<String, String>()
            if (GlobalUserData.email != emailInput.text.toString()) {
                //updateEmail(emailInput.text.toString())
                //userDataAuth["email"] = emailInput.text.toString()
            }
            if (GlobalUserData.password != passwordInput.text.toString()) {
                updatePassword(passwordInput.text.toString())
            }
            if (GlobalUserData.address != addressInput.text.toString()) {
                userDataAuth["address"] = addressInput.text.toString()
            }
            if (GlobalUserData.phone != phoneInput.text.toString()) {
                userDataAuth["phone"] = phoneInput.text.toString()
            }

            if (userDataAuth.isEmpty()) {
                return@setOnClickListener
            } else {
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



    private fun doPhotoUpload(photoUri: Uri) {
        val storage = Firebase.storage

// Create a reference to the location where you want to store the photo
        val photoRef = storage.reference.child("images/${GlobalUserData.uid}")

// Upload the photo to Firebase Storage
        val uploadTask = photoRef.putFile(photoUri) // 'photoUri' is the URI of the photo

// Listen for upload success or failure
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Photo uploaded successfully
            // Now, update the user document in Firestore with the URL of the uploaded photo
            val downloadUrl = taskSnapshot.storage.downloadUrl.toString()
            updateUserProfilePhotoInFirestore(GlobalUserData.uid, downloadUrl)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
            // Handle any errors
        }
    }

    private fun updateUserProfilePhotoInFirestore(userId: String, photoUrl: String) {
        val db = Firebase.firestore
        val updates = HashMap<String, String>()
        updates["profilePic"] = photoUrl
        val userRef = db.collection("users").whereEqualTo("UID", GlobalUserData.uid)

        // Update the 'photoUrl' field of the user document
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
                       GlobalUserData.profilePic = photoUrl

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
    }
}

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
       var db = Firebase.firestore
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
