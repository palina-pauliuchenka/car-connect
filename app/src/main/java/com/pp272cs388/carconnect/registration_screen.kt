package com.pp272cs388.carconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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
    private lateinit var storage: FirebaseStorage
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize Views
        val fullName_asset = findViewById<EditText>(R.id.fullNameInput)
        val email_asset = findViewById<EditText>(R.id.emailInput)
        val password_asset = findViewById<EditText>(R.id.passwordInput)
        val phoneNumber_asset = findViewById<EditText>(R.id.phoneNumberInput)
        val driveSpinner_asset = findViewById<Spinner>(R.id.driveSpinner)
        val genderSpinner_asset = findViewById<Spinner>(R.id.genderSpinner)
        val homeAddress_asset = findViewById<EditText>(R.id.homeAddressInput)
        val signUpButton = findViewById<Button>(R.id.createAccountButton)

        // Sign-Up Button Click Listener
        signUpButton.setOnClickListener {
            val fullName = fullName_asset.text.toString().trim()
            val email = email_asset.text.toString().trim()
            val password = password_asset.text.toString().trim()
            val phoneNumber = phoneNumber_asset.text.toString().trim()
            val driveChoice = driveSpinner_asset.selectedItem.toString()
            val genderPreference = genderSpinner_asset.selectedItem.toString()
            val homeAddress = homeAddress_asset.text.toString().trim()

            signUpUser(fullName, email, password, phoneNumber, driveChoice, genderPreference, homeAddress)
        }

    }

    // Sign-Up New User
    private fun signUpUser(fullName: String,
                           email: String,
                           password: String,
                           phoneNumber: String,
                           driveChoice: String,
                           genderPreference:String,
                           homeAddress: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                Toast.makeText(this, "Sign-up successful! Verification email sent.", Toast.LENGTH_SHORT).show()
                                // Clear input fields
                                findViewById<EditText>(R.id.emailEditText).text.clear()
                                findViewById<EditText>(R.id.passwordEditText).text.clear()
                                startActivity(Intent(this, main_screen::class.java))
                            } else {
                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

        val userId = auth.currentUser?.uid ?: return

        val userProfile = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "driveChoice" to driveChoice,
            "genderPreference" to genderPreference,
            "homeAddress" to homeAddress,
        )

        firestore.collection("users").document(userId).set(userProfile)
            .addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to create account: ${task.exception?.message}!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
