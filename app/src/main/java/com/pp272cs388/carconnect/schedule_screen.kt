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
import java.sql.Time


class schedule_screen : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private lateinit var userName: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        firestore = FirebaseFirestore.getInstance()

        val dateButton = findViewById<Button>(R.id.dateButton)
        val timeButton = findViewById<Button>(R.id.timeButton)
        val destinationSpinner = findViewById<Spinner>(R.id.destinationSpinner)
        val submitButton = findViewById<Button>(R.id.submitButton)

        val receivedString = intent.getStringExtra("PARAM_KEY") ?: "Default Value"

        // Populate spinner with destinations (home or school)
        val destinations = arrayOf("141 Summit St. 07103", "156-182 Warren St. 07102") // Home, School
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        destinationSpinner.adapter = spinnerAdapter


        val currentUserId = auth.currentUser?.uid ?: ""
        firestore.collection("users").document(currentUserId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userName = document.getString("fullName") ?: "N/A"
                }
            }


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

            val eta: Timestamp = combineDateAndTime(selectedDate, selectedTime)
            // Toast.makeText(this, "${eta}", Toast.LENGTH_SHORT).show()

            if (receivedString == "Pedestrian")
                fetchDrivers(eta, selectedDestination)
            else if (receivedString == "Driver")
                fetchPedestrians(eta, selectedDestination)
        }
    }

    private fun fetchPedestrians(eta: Timestamp, destination: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        firestore.collection("users")
            .whereEqualTo("driveChoice", "No")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // val peds = mutableListOf<String>()
                var pedName = ""
                var pedId = ""
                for (document in querySnapshot) {
                    if (document.id == userId) {
                        continue
                    }

                    pedName = document.getString("fullName") ?: "Unknown Pedestrian"
                    pedId = document.id
                    // drivers.add("$driverName - $carName")
                }

                updateRideHistory(userId, pedName, pedId, destination, eta, false)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching drivers: ${exception.message}")
                Toast.makeText(this, "Failed to fetch drivers.", Toast.LENGTH_SHORT).show()
            }

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
                    updateRideHistory(userId, driverName, driverId, destination, eta, true)
                    updateRideHistory(driverId, userName, userId, destination, eta, false) // Update the driver name here
                    // showDrivers(drivers)

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

    private fun updateRideHistory(currentUserId: String?, driverName: String, driverId: String, selectedDestination: String, eta: Timestamp, type: Boolean) {
        if (currentUserId == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new ride entry

        var rideEntry = mapOf(
            "ETA" to eta,
            "destination" to selectedDestination,
            "driverId" to driverId,
            "driverName" to driverName
        )

        if (!type) {
            rideEntry = mapOf(
                "ETA" to eta,
                "destination" to selectedDestination,
                "pedId" to driverId,
                "pedName" to driverName
            )
        }

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

    fun combineDateAndTime(selectedDate: String, selectedTime: String): Timestamp {
        // Parse the selectedDate (e.g., "December 12, 2024")
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
        val parsedDate = dateFormat.parse(selectedDate)

        // Parse the selectedTime (e.g., "2:30 PM")
        val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        val parsedTime = timeFormat.parse(selectedTime)

        // Use Calendar to combine the two
        val calendar = Calendar.getInstance()

        // Set the year, month, day from the parsedDate
        calendar.time = parsedDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Set the hour and minute from the parsedTime
        calendar.time = parsedTime
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Combine everything into a single Calendar instance
        calendar.set(year, month, day, hour, minute, 0) // Seconds = 0
        calendar.set(Calendar.MILLISECOND, 0) // Optional: clear milliseconds

        // Convert to Firebase Timestamp
        return Timestamp(calendar.time)
    }

}
