package com.example.donorsync

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class PostDriveActivity : AppCompatActivity() {

    private lateinit var venueInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var eligibilityInput: EditText
    private lateinit var requirementsInput: EditText
    private lateinit var notesInput: EditText

    // Firebase variables
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_drive)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        venueInput = findViewById(R.id.venue_input)
        dateInput = findViewById(R.id.date_input)
        timeInput = findViewById(R.id.time_input)
        eligibilityInput = findViewById(R.id.eligibility_input)
        requirementsInput = findViewById(R.id.requirements_input)
        notesInput = findViewById(R.id.notes_input)
    }

    private fun setupClickListeners() {
        // Date picker
        dateInput.setOnClickListener { showDatePicker() }

        // Time picker
        timeInput.setOnClickListener { showTimePicker() }

        // Post button
        val postButton = findViewById<Button>(R.id.post_button)
        postButton.setOnClickListener { postDrive() }

        // Close button
        val closeButton = findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener { finish() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                dateInput.setText("$day/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                timeInput.setText(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun postDrive() {
        if (validateForm()) {
            // Get current organization ID (using Firebase Auth)
            val currentUser = auth.currentUser
            val orgId = if (currentUser != null) {
                currentUser.uid
            } else {
                // For testing without authentication, use a default ID
                "test_organization_123"
            }

            // Create a unique ID for this blood drive
            val driveId = database.child("bloodDrives").push().key ?: ""

            val bloodDriveData = BloodDriveData(
                id = driveId,
                organizationId = orgId,
                venue = venueInput.text.toString(),
                date = dateInput.text.toString(),
                time = timeInput.text.toString(),
                eligibility = eligibilityInput.text.toString(),
                requirements = requirementsInput.text.toString(),
                notes = notesInput.text.toString(),
                postedDate = System.currentTimeMillis(),
                status = "active"
            )

            // Save to Firebase Realtime Database
            database.child("bloodDrives").child(driveId).setValue(bloodDriveData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Blood Drive Posted Successfully!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to post: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (venueInput.text.isEmpty()) {
            venueInput.error = "Venue is required"
            isValid = false
        }

        if (dateInput.text.isEmpty()) {
            dateInput.error = "Date is required"
            isValid = false
        }

        if (timeInput.text.isEmpty()) {
            timeInput.error = "Time is required"
            isValid = false
        }

        return isValid
    }
}