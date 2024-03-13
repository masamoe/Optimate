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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.example.optimate.R
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class ModuleChoosingMain : AppCompatActivity() {
    private var db = Firebase.firestore
    //lateinit var paymentSheet: PaymentSheet
    //lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    //lateinit var paymentIntentClientSecret: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module_choosing_main)
        val uid = intent.getStringExtra("USER_UID")
        val container1 = findViewById<TextView>(R.id.container1)
        val container2 = findViewById<TextView>(R.id.container2)

        val payButtonMain = findViewById<Button>(R.id.payButtonMain)
        var container1Picked = false
        var container2Picked = false

        val module1Price = 10.00
        val module2Price = 30.00

        var currentAmount = 0.00
        val amount = findViewById<TextView>(R.id.Amount)
        val moduleList = mutableListOf<String>()
        //paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        //fetchPaymentIntent()

        val callback = object : OnBackPressedCallback(true /* default to enabled */) {
            override fun handleOnBackPressed() {
                intent.putExtra("USER_UID", uid)
                // Start the same activity again
                val intent = Intent(this@ModuleChoosingMain, ModuleChoosingMain::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)



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



        payButtonMain.setOnClickListener {
            if(container1Picked){
                moduleList.add("Basic")

                if (container2Picked){
                    moduleList.add("Plus")
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
            GlobalUserData.uid = user

            // Navigate to the Login activity
            //presentPaymentSheet()

            val intent = Intent(this, PaymentConfirm::class.java)
            startActivity(intent)
            finish()
            // Finish the current activity so the user can't go back to it
        } else {
            // User is null, stay on the register page or show an error message
            Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(uid: String, newModules: List<String>, currentAmount: Double) {
        Log.e(this.toString(), "updateUser: called ", )
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
   /* fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                print("Completed")
                val intent = Intent(this, PaymentConfirm::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Optimate",
                customer = customerConfig,
                // Set `allowsDelayedPaymentMethods` to true if your business handles
                // delayed notification payment methods like US bank accounts.
                allowsDelayedPaymentMethods = true
            )
        )
    }

    private fun fetchPaymentIntent() {
        // Fetch payment intent client secret and customer config from your backend
        // This is just a placeholder, replace it with your actual network request code
        "https://optimateserver.onrender.com/payment-sheet".httpPost().responseJson { _, _, result ->
            if (result is Result.Success) {
                val responseJson = result.get().obj()
                paymentIntentClientSecret = responseJson.getString("paymentIntent")
                customerConfig = PaymentSheet.CustomerConfiguration(
                    responseJson.getString("customer"),
                    responseJson.getString("ephemeralKey")
                )
                val publishableKey = responseJson.getString("publishableKey")
                PaymentConfiguration.init(this, publishableKey)
            }
        }
    }*/
}