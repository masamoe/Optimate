package com.example.optimate.loginAndRegister
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

class Register : AppCompatActivity() {
    // Declare FirebaseAuth instance
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        // Get references to the EditTexts in the layout
        val emailEditText = findViewById<EditText>(R.id.registerEmail)
        val passwordEditText= findViewById<EditText>(R.id.registerPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.registerConfirmPassword)
        val addressEditText = findViewById<EditText>(R.id.registerAddress)
        val nameEditText = findViewById<EditText>(R.id.registerBusinessName)
        // Find the register button and set an OnClickListener
        val registerButton = findViewById<Button>(R.id.registerBtn)
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            // Check for non-empty fields
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for password length
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proceed with creating the user if all checks pass
            createNewUser(email, password, address,name)
        }
    }

    // Create a separate function to handle user registration
    private fun createNewUser(email: String, password: String, address: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        addUserToDB(email, address, name, user.uid)
                        // Update UI after user creation and addition to the database
                        updateUI(user)
                    } else {
                        // Handle the case where user is null
                        Toast.makeText(this@Register, "User is null.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (task.exception?.message?.contains("already in use") == true) {
                        Toast.makeText(
                            this@Register,
                            "Email is already registered. Please use a different email.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@Register,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in, show success message
            Toast.makeText(this, "Welcome to OptiMate!", Toast.LENGTH_SHORT).show()

            // Navigate to the Login activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Finish the current activity so the user can't go back to it
        } else {
            // User is null, stay on the register page or show an error message
            Toast.makeText(this, "Register failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToDB(email: String, address: String, name: String, uid: String){

        val bid = uid + name.substring(0,2)

        val user = hashMapOf(
            "email" to email,
            "address" to address,
            "name" to name,
            "title" to "businessOwner",
            "role" to "businessOwner",
            "UID" to uid,
            "BID" to bid
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Register Success. ${documentReference.id}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Register failed. ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun reload() {
        // Reload the current activity or perform other actions if the user is already signed in
    }
}