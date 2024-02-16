package com.example.optimate.employeeFlow

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.optimate.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch


class Availability : AppCompatActivity() {

    private var isEditing = false

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
            }
        }

        // Monday
        disableEnableMondays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleMondays)
        }

// Tuesday
        disableEnableTuesdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleTuesdays)
        }

// Wednesday
        disableEnableWednesdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleWednesdays)
        }

// Thursday
        disableEnableThursdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleThursdays)
        }

// Friday
        disableEnableFridays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleFridays)
        }

// Saturday
        disableEnableSaturdays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleSaturdays)
        }

// Sunday
        disableEnableSundays.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleState(isChecked, toggleSundays)
        }



    }

    // Function to toggle the enabled state of a toggle and MaterialButtons based on the checked state of a checkbox
    private fun toggleState(isChecked: Boolean, toggle: MaterialButtonToggleGroup) {
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

        for (i in toggleList.indices) {
            toggleList[i].isEnabled = enable
            switchList[i].isEnabled = enable
        }
    }

}
