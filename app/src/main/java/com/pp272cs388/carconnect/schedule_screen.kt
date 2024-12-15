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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.Timestamp


class schedule_screen : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedDate: Timestamp
    private lateinit var selectedTime: Timestamp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        firestore = FirebaseFirestore.getInstance()

        val dateButton = findViewById<Button>(R.id.dateButton)
        val timeButton = findViewById<Button>(R.id.timeButton)
        val destinationSpinner = findViewById<Spinner>(R.id.destinationSpinner)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Populate spinner with destinations (home or school)
        val destinations = arrayOf("141 Summit St. 07103", "156-182 Warren St. 07102") // Home, School
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        destinationSpinner.adapter = spinnerAdapter

        // Date picker
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this,
                { _, year, month, day ->
                    // Set the selected date in Calendar instance
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)

                    // Convert Calendar to Date
                    val date = calendar.time

                    // Convert Date to Firebase Timestamp
                    val selectedDate = com.google.firebase.Timestamp(date)

                    // Update button text (formatted date for display)
                    val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
                    dateButton.text = sdf.format(date)

                    // Debugging or Log Output
                    Log.d("DatePicker", "Selected Timestamp: $selectedDate")
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
                    // Set the time in the Calendar instance
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    // Convert the Calendar time to a Date object
                    val date = calendar.time

                    // Convert the Date object to a Firebase Timestamp
                    val selectedTime = com.google.firebase.Timestamp(date)

                    // Update button text (optional, for display)
                    val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                    timeButton.text = sdf.format(date)

                    // Debugging or Log Output
                    Log.d("TimePicker", "Selected Timestamp: $selectedTime")
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 12-hour format
            )
            timePickerDialog.show()
        }

        // Submit button action
        submitButton.setOnClickListener {
            val selectedDestination = destinationSpinner.selectedItem.toString()
            val eta = combineDateAndTime(selectedDate, selectedTime)
            fetchDrivers(eta, selectedDestination)
        }
    }

    private fun fetchPedestrians(date: String, time: String, destination: String) {

    }

    private fun fetchDrivers(eta: Timestamp, destination: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        firestore.collection("users")
            .whereEqualTo("driveChoice", "Yes")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val drivers = mutableListOf<String>()
                var carName = ""
                var driverName = ""
                var driverId = ""
                for (document in querySnapshot) {
                    if (document.id == userId) {
                        continue
                    }

                    carName = document.getString("carName") ?: "Unknown Car"
                    driverName = document.getString("fullName") ?: "Unknown Driver"
                    driverId = document.id
                    drivers.add("$driverName - $carName")
                }

                if (drivers.isNotEmpty()) {
                    // Display and save the drivers
                    updateRideHistory(userId, driverName, driverId, destination, eta)
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

    private fun updateRideHistory(currentUserId: String?, driverName: String, driverId: String, selectedDestination: String, eta: Timestamp) {
        if (currentUserId == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new ride entry
        val rideEntry = mapOf(
            "ETA" to eta,
            "destination" to selectedDestination,
            "driverId" to driverId,
            "driverName" to driverName
        )

        // Add to the current user's rideHistory
        val userRef = firestore.collection("users").document(currentUserId)
        userRef.update("rideHistory", FieldValue.arrayUnion(rideEntry))
            .addOnSuccessListener {
                Toast.makeText(this, "Ride history updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error updating ride history: ${exception.message}")
                Toast.makeText(this, "Failed to update ride history.", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showDrivers(drivers: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Available Drivers")
        builder.setItems(drivers.toTypedArray()) { _, _ -> }
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun combineDateAndTime(dateTimestamp: Timestamp, timeTimestamp: Timestamp): Timestamp {
        // Convert Firebase Timestamps to Date objects
        val date = dateTimestamp.toDate()
        val time = timeTimestamp.toDate()

        // Extract date components
        val calendarDate = Calendar.getInstance()
        calendarDate.time = date
        val year = calendarDate.get(Calendar.YEAR)
        val month = calendarDate.get(Calendar.MONTH)
        val day = calendarDate.get(Calendar.DAY_OF_MONTH)

        // Extract time components
        val calendarTime = Calendar.getInstance()
        calendarTime.time = time
        val hour = calendarTime.get(Calendar.HOUR_OF_DAY)
        val minute = calendarTime.get(Calendar.MINUTE)
        val second = calendarTime.get(Calendar.SECOND)

        // Combine date and time components into a new Calendar instance
        val combinedCalendar = Calendar.getInstance()
        combinedCalendar.set(Calendar.YEAR, year)
        combinedCalendar.set(Calendar.MONTH, month)
        combinedCalendar.set(Calendar.DAY_OF_MONTH, day)
        combinedCalendar.set(Calendar.HOUR_OF_DAY, hour)
        combinedCalendar.set(Calendar.MINUTE, minute)
        combinedCalendar.set(Calendar.SECOND, second)
        combinedCalendar.set(Calendar.MILLISECOND, 0) // Optional: Clear milliseconds

        // Convert the combined Calendar back to a Timestamp
        return Timestamp(combinedCalendar.time)
    }

}
