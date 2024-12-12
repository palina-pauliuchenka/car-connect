package com.pp272cs388.carconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class registration_screen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize Views
        val fullNameAsset = findViewById<EditText>(R.id.fullNameInput)
        val emailAsset = findViewById<EditText>(R.id.emailInput)
        val passwordAsset = findViewById<EditText>(R.id.passwordInput)
        val phoneNumberAsset = findViewById<EditText>(R.id.phoneNumberInput)
        val driveSpinnerAsset = findViewById<Spinner>(R.id.driveSpinner)
        val genderSpinnerAsset = findViewById<Spinner>(R.id.genderSpinner)
        val homeAddressAsset = findViewById<EditText>(R.id.homeAddressInput)
        val signUpButton = findViewById<Button>(R.id.createAccountButton)

        // Sign-Up Button Click Listener
        signUpButton.setOnClickListener {
            val fullName = fullNameAsset.text.toString().trim()
            val email = emailAsset.text.toString().trim()
            val password = passwordAsset.text.toString().trim()
            val phoneNumber = phoneNumberAsset.text.toString().trim()
            val driveChoice = driveSpinnerAsset.selectedItem.toString()
            val genderPreference = genderSpinnerAsset.selectedItem.toString()
            val homeAddress = homeAddressAsset.text.toString().trim()

            signUpUser(fullName, email, password, phoneNumber, driveChoice, genderPreference, homeAddress)
        }
    }

    private fun registerUser(email: String, password: String, onSuccess: (userId: String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show()
                            user.uid.let {
                                Log.d("FirestoreDebug", "Registered User ID: $it")
                                onSuccess(it) // Pass the userId to the callback
                            }
                        } else {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateProfile(
        userId: String,
        fullName: String,
        email: String,
        phoneNumber: String,
        driveChoice: String,
        genderPreference: String,
        homeAddress: String
    ) {
        val userProfile = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "driveChoice" to driveChoice,
            "genderPreference" to genderPreference,
            "homeAddress" to homeAddress
        )

        firestore.collection("users").document(userId).set(userProfile)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity
                } else {
                    Toast.makeText(this, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signUpUser(
        fullName: String,
        email: String,
        password: String,
        phoneNumber: String,
        driveChoice: String,
        genderPreference: String,
        homeAddress: String
    ) {
        registerUser(email, password) { userId ->
            updateProfile(userId, fullName, email, phoneNumber, driveChoice, genderPreference, homeAddress)
        }
    }
}
