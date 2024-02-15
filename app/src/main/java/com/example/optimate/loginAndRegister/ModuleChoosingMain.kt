package com.example.optimate.loginAndRegister

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.optimate.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import java.util.Date

class ModuleChoosingMain : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module_choosing_main)
        val uid = intent.getStringExtra("USER_UID")
        val container1 = findViewById<TextView>(R.id.container1)
        val container2 = findViewById<TextView>(R.id.container2)
        val container3 = findViewById<TextView>(R.id.container3)
        val payButton = findViewById<Button>(R.id.payButton)
        var container1Picked = false
        var container2Picked = false
        var container3Picked = false
        val module1Price = 10.00
        val module2Price = 10.00
        val module3Price = 10.00
        var currentAmount = 0.00
        val amount = findViewById<TextView>(R.id.Amount)
        val moduleList = mutableListOf<String>()

        


        container1.setOnClickListener {
            changeColor(container1)

            if (container1Picked) {
                currentAmount -= module1Price
            } else {
                currentAmount += module1Price
            }
            amount.text = "$" + currentAmount.toString()
            container1Picked = !container1Picked
        }

        container2.setOnClickListener {
            changeColor(container2)

            if (container2Picked) {
                currentAmount -= module2Price
            } else {
                currentAmount += module2Price
            }
            amount.text = "$" + currentAmount.toString()
            container2Picked = !container2Picked
        }

        container3.setOnClickListener {
            changeColor(container3)

            if (container3Picked) {
                currentAmount -= module3Price
            } else {
                currentAmount += module3Price
            }
            amount.text = "$" + currentAmount.toString()
            container3Picked = !container3Picked
        }

        payButton.setOnClickListener {
            if(container1Picked){
                moduleList.add("One")

                if (container2Picked){
                    moduleList.add("Two")
                }
                if (container3Picked){
                    moduleList.add("Three")
                }

                if (uid != null) {
                    updateUser(uid, moduleList, currentAmount)
                }

            } else{
                Toast.makeText(this, "You need the Basic Plan to continue", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun changeColor(view: View) {
        val defaultColor = ContextCompat.getColor(this, R.color.light_grey)
        val clickedColor = ContextCompat.getColor(this, R.color.grey)// Change this to the appropriate color resource

        val currentColor = (view.background as? ColorDrawable)?.color ?: defaultColor
        val newColor = if(currentColor == defaultColor) clickedColor else defaultColor
        view.setBackgroundColor(newColor)
    }

    private fun updateUI(user: String,newModules: List<String>, currentAmount: Double) {
        if (newModules != null) {
            // User is signed in, show success message

            val intent = Intent(this, PaymentConfirm::class.java)
            intent.putExtra("USER_UID", user)
            intent.putExtra("paymentAmount", currentAmount)
            // Navigate to the Login activity


            startActivity(intent)
            finish() // Finish the current activity so the user can't go back to it
        } else {
            // User is null, stay on the register page or show an error message
            Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(uid: String, newModules: List<String>, currentAmount: Double) {

        db.collection("users")
            .whereEqualTo("UID", uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.e("EditAccountActivity", "No matching account found")
                    return@addOnSuccessListener
                }

                val account = documents.first()

                account.reference.update("modules", newModules)
                    .addOnSuccessListener {
                        Log.d("EditAccountActivity", "Account updated successfully")
                        updateUI(uid, newModules,currentAmount)
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditAccountActivity", "Error updating account", e)
                    }
            }
    }
    private fun reload() {
        // Reload the current activity or perform other actions if the user is already signed in
    }
}