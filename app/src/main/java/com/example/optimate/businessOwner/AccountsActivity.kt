package com.example.optimate.businessOwner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountsActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val bid = GlobalUserData.bid
    private val accountsList = mutableStateListOf<Account>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val accounts = remember { accountsList }
            AccountsScreen(accounts = accounts)
        }

        fetchAccounts(bid)
    }

    private fun fetchAccounts(bid: String) {
        db.collection("users")
            .whereEqualTo("BID", bid)
            .get()
            .addOnSuccessListener { documents ->
                val fetchedAccounts = mutableListOf<Account>()
                for (document in documents) {
                    val name = document.getString("name") ?: "N/A"
                    val title = document.getString("title") ?: "N/A"
                    fetchedAccounts.add(Account(name, title))
                }

                accountsList.clear()
                accountsList.addAll(fetchedAccounts)
            }
            .addOnFailureListener { exception ->
                // Handle the error appropriately
            }
    }
}
