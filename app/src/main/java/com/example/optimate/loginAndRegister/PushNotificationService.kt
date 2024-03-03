package com.example.optimate.loginAndRegister


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.optimate.businessOwner.Requests
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushNotificationService : FirebaseMessagingService() {
    private val db = Firebase.firestore

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        getToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Handle incoming messages here if needed
    }

    private fun getToken(token: String) {
        GlobalUserData.deviceToken = token

        // Update token in Firebase
        db.collection("users")
            .whereEqualTo("UID", GlobalUserData.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.e("EditAccountActivity", "No matching account found")
                    return@addOnSuccessListener
                }

                val account = documents.first()

                account.reference.update("deviceToken", token)
                    .addOnSuccessListener {
                        Log.d("EditAccountActivity", "Account Updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditAccountActivity", "Error Updating account", e)
                    }
            }
    }


}

