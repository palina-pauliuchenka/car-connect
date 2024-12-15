package com.pp272cs388.carconnect.main_screen_dir

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pp272cs388.carconnect.R
import com.pp272cs388.carconnect.registration_screen
import com.pp272cs388.carconnect.schedule_screen

class ProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        // Initialize Firebase Auth and Firestore
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val updateAccButton = view.findViewById<Button>(R.id.update_account)
        val updateCarButton = view.findViewById<Button>(R.id.update_car_info)
        // Let's first figure out the button update sequence
        updateAccButton.setOnClickListener {
            val intent = Intent(requireContext(), registration_screen::class.java)
            intent.putExtra("PARAM_KEY", "Update-User")
            startActivity(intent)
        }
        updateCarButton.setOnClickListener {
            val intent = Intent(requireContext(), schedule_screen::class.java)

            // intent.putExtra("PARAM_KEY", "Update-Car-Info")
            // startActivity(intent)
        }


        val profileInfoTextView = view.findViewById<TextView>(R.id.profileInfo)

        // Fetch user information from Firestore
        val userId = auth.currentUser?.uid

        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("FirestoreDebug", "Document retrieved: ${document.data}")
                        // Extract fields
                        val fullName = document.getString("fullName") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        // Update your UI
                    } else {
                        Log.d("FirestoreDebug", "No document found for user ID: $userId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreDebug", "Error fetching document: ${exception.message}")
                }
        } else {
            Log.d("FirestoreDebug", "User ID is null. User might not be logged in.")
        }


        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        val phoneNumber = document.getString("phoneNumber") ?: "N/A"
                        val homeAddress = document.getString("homeAddress") ?: "N/A"
                        val driveChoice = document.getString("driveChoice") ?: "N/A"
                        val genderPreference = document.getString("genderPreference") ?: "N/A"

                        // Update UI with the retrieved data
                        val profileInfo = """
                            Name: $fullName
                            Email: $email
                            Phone: $phoneNumber
                            Address: $homeAddress
                            Drive Choice: $driveChoice
                            Gender Preference: $genderPreference
                        """.trimIndent()

                        profileInfoTextView.text = profileInfo
                    } else {
                        profileInfoTextView.text = "No profile information available."
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to load profile: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            profileInfoTextView.text = "No user is logged in."
        }

        return view
    }
}
