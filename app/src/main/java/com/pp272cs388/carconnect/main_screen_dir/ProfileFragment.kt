package com.pp272cs388.carconnect.main_screen_dir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pp272cs388.carconnect.R

class ProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth and Firestore
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val profileInfoTextView = view.findViewById<TextView>(R.id.profileInfo)

        // Fetch user information from Firestore
        val userId = auth.currentUser?.uid
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
