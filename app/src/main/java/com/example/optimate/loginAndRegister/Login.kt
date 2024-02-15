package com.example.optimate.loginAndRegister
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.businessOwner.BusinessLanding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase


class Login : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private var registerBtn: Button? = null
    private lateinit var forgotPasswordClk: TextView
    private lateinit var loginBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        // Get references to the EditTexts and Buttons in the layout
        emailEditText = findViewById(R.id.loginEmail)
        passwordEditText = findViewById(R.id.loginPassword)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)
        forgotPasswordClk = findViewById(R.id.forgotPassword)
        // Set up the login button click listener
        loginBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Check for non-empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in with Firebase
            signInWithEmail(email, password)
            GlobalUserData.password = password
        }

        // Set up the register button click listener
        registerBtn?.setOnClickListener {
            // Go to the Register activity
            startActivity(Intent(this, Register::class.java))
        }
        forgotPasswordClk.setOnClickListener {
            startActivity(Intent(this, PasswordReset::class.java))
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    getUserData(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Go to your main activity or another activity after login
            val intent = Intent(this, ModuleChoosingMain::class.java)
            intent.putExtra("USER_UID", user.uid)
            startActivity(intent)
            finish()
        } else {
            // Stay on the login page or show error
        }
    }
    private fun getUserData(user: FirebaseUser?) {
        if (user != null) {
            val uid = user.uid
            val docRef = db.collection("users").whereEqualTo("UID", uid)
            docRef.get()
                .addOnSuccessListener { querySnapshot ->

                    for (document in querySnapshot.documents) {
                        // Access data for each document
                        GlobalUserData.name = (document.getString("name") ?: "").toString()
                        GlobalUserData.uid = (document.getString("UID") ?: "").toString()
                        GlobalUserData.bid = (document.getString("BID") ?: "").toString()
                        GlobalUserData.email = (document.getString("email") ?: "").toString()
                        GlobalUserData.address = (document.getString("address") ?: "").toString()
                        GlobalUserData.role = (document.getString("role") ?: "").toString()
                        GlobalUserData.title = (document.getString("title") ?: "").toString()
                        GlobalUserData.wage = (document.getDouble("wage") ?: 0.0).toFloat()
                        GlobalUserData.account_status.status =
                            (document.getString("status") ?: "").toString()
                        GlobalUserData.modules = listOf((document.getString("modules") ?: "").toString())
                    }
                    updateUI(user)
                }
                .addOnFailureListener {e ->
                    Toast.makeText(baseContext, "Get Failed $e",
                        Toast.LENGTH_SHORT).show()
                }

        }

    }
}


