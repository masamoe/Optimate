package com.example.optimate.employeeFlow

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

enum class AvailabilityStatus {
    MORNING,
    EVENING,
    ALL_DAY,
    NOT_AVAILABLE
}


class Availability : AppCompatActivity() {

    private var db = Firebase.firestore

    private var isEditing = false


    private val availabilityMap = mutableMapOf(
        "Monday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE),
        "Tuesday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE),
        "Wednesday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE),
        "Thursday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE),
        "Friday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE),
        "Saturday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE),
        "Sunday" to mutableListOf<AvailabilityStatus>(AvailabilityStatus.NOT_AVAILABLE)
    )

    private lateinit var toggleMondays: MaterialButtonToggleGroup
    private lateinit var toggleTuesdays: MaterialButtonToggleGroup
    private lateinit var toggleWednesdays: MaterialButtonToggleGroup
    private lateinit var toggleThursdays: MaterialButtonToggleGroup
    private lateinit var toggleFridays: MaterialButtonToggleGroup
    private lateinit var toggleSaturdays: MaterialButtonToggleGroup
    private lateinit var toggleSundays: MaterialButtonToggleGroup

    private lateinit var disableEnableMondays: MaterialSwitch
    private lateinit var disableEnableTuesdays: MaterialSwitch
    private lateinit var disableEnableWednesdays: MaterialSwitch
    private lateinit var disableEnableThursdays: MaterialSwitch
    private lateinit var disableEnableFridays: MaterialSwitch
    private lateinit var disableEnableSaturdays: MaterialSwitch
    private lateinit var disableEnableSundays: MaterialSwitch


    // Buttons for Monday
    private lateinit var mondaysM: MaterialButton
    private lateinit var mondaysE: MaterialButton
    private lateinit var mondaysA: MaterialButton

    // Buttons for Tuesday
    private lateinit var tuesdaysM: MaterialButton
    private lateinit var tuesdaysE: MaterialButton
    private lateinit var tuesdaysA: MaterialButton

    // Buttons for Wednesday
    private lateinit var wednesdaysM: MaterialButton
    private lateinit var wednesdaysE: MaterialButton
    private lateinit var wednesdaysA: MaterialButton

    // Buttons for Thursday
    private lateinit var thursdaysM: MaterialButton
    private lateinit var thursdaysE: MaterialButton
    private lateinit var thursdaysA: MaterialButton

    // Buttons for Friday
    private lateinit var fridaysM: MaterialButton
    private lateinit var fridaysE: MaterialButton
    private lateinit var fridaysA: MaterialButton

    // Buttons for Saturday
    private lateinit var saturdaysM: MaterialButton
    private lateinit var saturdaysE: MaterialButton
    private lateinit var saturdaysA: MaterialButton

    // Buttons for Sunday
    private lateinit var sundaysM: MaterialButton
    private lateinit var sundaysE: MaterialButton
    private lateinit var sundaysA: MaterialButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_availability)

        val editButton = findViewById<MaterialButton>(R.id.editButton)
        val saveIcon = ContextCompat.getDrawable(this, R.drawable.ic_save)
        val editIcon = ContextCompat.getDrawable(this, R.drawable.ic_settings)


        toggleMondays = findViewById(R.id.toggleMondays)
        toggleTuesdays = findViewById(R.id.toggleTuesdays)
        toggleWednesdays = findViewById(R.id.toggleWednesdays)
        toggleThursdays = findViewById(R.id.toggleThursdays)
        toggleFridays = findViewById(R.id.toggleFridays)
        toggleSaturdays = findViewById(R.id.toggleSaturdays)
        toggleSundays = findViewById(R.id.toggleSundays)

        disableEnableMondays = findViewById(R.id.disableEnableMondays)
        disableEnableTuesdays = findViewById(R.id.disableEnableTuesdays)
        disableEnableWednesdays = findViewById(R.id.disableEnableWednesdays)
        disableEnableThursdays = findViewById(R.id.disableEnableThursdays)
        disableEnableFridays = findViewById(R.id.disableEnableFridays)
        disableEnableSaturdays = findViewById(R.id.disableEnableSaturdays)
        disableEnableSundays = findViewById(R.id.disableEnableSundays)


        // Find and initialize buttons for each day
        mondaysM = findViewById(R.id.mondaysM)
        mondaysE = findViewById(R.id.mondaysE)
        mondaysA = findViewById(R.id.mondaysA)

        tuesdaysM = findViewById(R.id.tuesdaysM)
        tuesdaysE = findViewById(R.id.tuesdaysE)
        tuesdaysA = findViewById(R.id.tuesdaysA)

        wednesdaysM = findViewById(R.id.wednesdaysM)
        wednesdaysE = findViewById(R.id.wednesdaysE)
        wednesdaysA = findViewById(R.id.wednesdaysA)

        thursdaysM = findViewById(R.id.thursdaysM)
        thursdaysE = findViewById(R.id.thursdaysE)
        thursdaysA = findViewById(R.id.thursdaysA)

        fridaysM = findViewById(R.id.fridaysM)
        fridaysE = findViewById(R.id.fridaysE)
        fridaysA = findViewById(R.id.fridaysA)

        saturdaysM = findViewById(R.id.saturdaysM)
        saturdaysE = findViewById(R.id.saturdaysE)
        saturdaysA = findViewById(R.id.saturdaysA)

        sundaysM = findViewById(R.id.sundaysM)
        sundaysE = findViewById(R.id.sundaysE)
        sundaysA = findViewById(R.id.sundaysA)


        // Set initial state
        setEditingState(editButton, editIcon)

        editButton.setOnClickListener {
            isEditing = !isEditing
            if (isEditing) {
                // Change to "Save" and switch to save icon
                editButton.text = "Save"
                editButton.icon = saveIcon
                enableButtons(true)

            } else {
                // Change to "Edit" and switch to edit icon
                editButton.text = "Edit"
                editButton.icon = editIcon
                enableButtons(false)
                updateUser(availabilityMap)

            }
        }

        // Monday
        disableEnableMondays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleMondays)
            availabilityMap["Monday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }

// Tuesday
        disableEnableTuesdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleTuesdays)
            availabilityMap["Tuesday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }

// Wednesday
        disableEnableWednesdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleWednesdays)
            availabilityMap["Wednesday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }

// Thursday
        disableEnableThursdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleThursdays)
            availabilityMap["Thursday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }

// Friday
        disableEnableFridays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleFridays)
            availabilityMap["Friday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }

// Saturday
        disableEnableSaturdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleSaturdays)
            availabilityMap["Saturday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }

// Sunday
        disableEnableSundays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleSundays)
            availabilityMap["Sunday"]?.apply {
                clear()
                add(AvailabilityStatus.NOT_AVAILABLE)
            }
        }


        // Monday
        mondaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Monday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        mondaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Monday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        mondaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Monday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }

// Tuesday
        tuesdaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Tuesday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        tuesdaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Tuesday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        tuesdaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Tuesday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }

        // Wednesday
        wednesdaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Wednesday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        wednesdaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Wednesday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        wednesdaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Wednesday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }

//Thursday
        thursdaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Thursday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        thursdaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Thursday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        thursdaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Thursday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }
        // Friday
        fridaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Friday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        fridaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Friday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        fridaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Friday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }

// Saturday
        saturdaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Saturday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        saturdaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Saturday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        saturdaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Saturday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }
        // Sunday
        sundaysM.setOnClickListener {
            if (isEditing) {
                availabilityMap["Sunday"]?.apply {
                    clear()
                    add(AvailabilityStatus.MORNING)
                }
            }
        }

        sundaysE.setOnClickListener {
            if (isEditing) {
                availabilityMap["Sunday"]?.apply {
                    clear()
                    add(AvailabilityStatus.EVENING)
                }
            }
        }

        sundaysA.setOnClickListener {
            if (isEditing) {
                availabilityMap["Sunday"]?.apply {
                    clear()
                    add(AvailabilityStatus.ALL_DAY)
                }
            }
        }

// Similarly, add click listeners for buttons representing other days and time slots...




    }

    // Function to toggle the enabled state of a toggle and MaterialButtons based on the checked state of a checkbox
    private fun toggleState(isChecked: Boolean, toggle: MaterialButtonToggleGroup) {
        if (!isChecked) {
            toggle.clearChecked()
        }
        toggle.isEnabled = isChecked

    }


    private fun setEditingState(editButton: MaterialButton, icon: Drawable?) {
        editButton.text = if (isEditing) "Save" else "Edit"
        editButton.icon = icon
        enableButtons(isEditing)
    }

    private fun enableButtons(enable: Boolean) {

        val toggleList = listOf(
            toggleMondays,
            toggleTuesdays,
            toggleWednesdays,
            toggleThursdays,
            toggleFridays,
            toggleSaturdays,
            toggleSundays
        )

        val switchList = listOf(
            disableEnableMondays,
            disableEnableTuesdays,
            disableEnableWednesdays,
            disableEnableThursdays,
            disableEnableFridays,
            disableEnableSaturdays,
            disableEnableSundays
        )

        for (i in switchList.indices) {
            toggleList[i].isEnabled = enable && switchList[i].isChecked
            switchList[i].isEnabled = enable
        }
    }


    private fun updateUser(availabilityMap: MutableMap<String, MutableList<AvailabilityStatus>>) {
        // Query the availability collection for documents with the specified UID
        db.collection("availability")
            .whereEqualTo("UID", GlobalUserData.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No matching document found for the provided UID
                    Log.d("EditAccountActivity", "No matching account found. Creating new record.")
                    // Create a new record with the provided UID and availability data
                    val newAvailability = hashMapOf(
                        "UID" to GlobalUserData.uid,
                        "BID" to GlobalUserData.bid,
                        "name" to GlobalUserData.name,
                        "availability" to availabilityMap
                    )
                    db.collection("availability")
                        .add(newAvailability)
                        .addOnSuccessListener { documentReference ->
                            Log.d("EditAccountActivity", "New record created with ID: ${documentReference.id}")
                            // You may perform additional actions here if needed
                        }
                        .addOnFailureListener { e ->
                            Log.e("EditAccountActivity", "Error creating new record", e)
                        }
                    return@addOnSuccessListener
                }

                // If at least one document is found, proceed with updating the existing record
                val account = documents.first()
                account.reference.update("availability", availabilityMap)
                    .addOnSuccessListener {
                        Log.d("EditAccountActivity", "Account updated successfully")
                        // You may perform additional actions here if needed
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditAccountActivity", "Error updating account", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("EditAccountActivity", "Error fetching account", e)
            }
    }

}
