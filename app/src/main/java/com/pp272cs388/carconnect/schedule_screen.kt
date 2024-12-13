package com.pp272cs388.carconnect

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class schedule_screen : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedDate: String
    private lateinit var selectedTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        firestore = FirebaseFirestore.getInstance()

        val dateButton = findViewById<Button>(R.id.dateButton)
        val timeButton = findViewById<Button>(R.id.timeButton)
        val destinationSpinner = findViewById<Spinner>(R.id.destinationSpinner)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Populate spinner with destinations (home or school)
        val destinations = arrayOf("Home", "School")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        destinationSpinner.adapter = spinnerAdapter

        // Date picker
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this,
                { _, year, month, day ->
                    val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
                    calendar.set(year, month, day)
                    selectedDate = sdf.format(calendar.time)
                    dateButton.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Time picker
        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(this,
                { _, hour, minute ->
                    val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    selectedTime = sdf.format(calendar.time)
                    timeButton.text = selectedTime
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePickerDialog.show()
        }

        // Submit button action
        submitButton.setOnClickListener {
            val selectedDestination = destinationSpinner.selectedItem.toString()
            fetchDrivers(selectedDate, selectedTime, selectedDestination)
        }
    }

    private fun fetchPedestrians(date: String, time: String, destination: String) {

    }

    private fun fetchDrivers(date: String, time: String, destination: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        firestore.collection("users")
            .whereEqualTo("driveChoice", "Yes")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val drivers = mutableListOf<String>()
                for (document in querySnapshot) {
                    if (document.id == userId) {
                        continue
                    }

                    val carName = document.getString("carName") ?: "Unknown Car"
                    val fullName = document.getString("fullName") ?: "Unknown Driver"
                    drivers.add("$fullName - $carName")
                }

                if (drivers.isNotEmpty()) {
                    // Display the drivers
                    showDrivers(drivers)
                } else {
                    // No available drivers
                    Toast.makeText(this, "No available drivers found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching drivers: ${exception.message}")
                Toast.makeText(this, "Failed to fetch drivers.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDrivers(drivers: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Available Drivers")
        builder.setItems(drivers.toTypedArray()) { _, _ -> }
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
