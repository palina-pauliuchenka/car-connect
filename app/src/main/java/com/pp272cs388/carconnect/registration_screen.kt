package com.pp272cs388.carconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

class registration_screen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

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

            if (validateInputs(email, password)) {
                signUpUser(fullName, email, password, phoneNumber, driveChoice, genderPreference, homeAddress)
            }
        }

    }

    // Validate email and password inputs
    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || !email.endsWith(".edu")) {
            Toast.makeText(this, "Please enter a valid university email address.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
    }
}
