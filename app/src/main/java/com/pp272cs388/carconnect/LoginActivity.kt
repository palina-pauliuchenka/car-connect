package com.pp272cs388.carconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput          = findViewById<EditText>(R.id.emailEditText)
        val loginSignupButton   = findViewById<Button>(R.id.loginSignupButton)

        loginSignupButton.setOnClickListener {
            val email       = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your university email address.", Toast.LENGTH_SHORT).show()
            } else if(!email.endsWith(".edu")) {
                Toast.makeText(this, "Only .edu email addresses are allowed.", Toast.LENGTH_SHORT).show()
            } else {
                // Toast.makeText(this, "Moving further", Toast.LENGTH_SHORT).show()
                checkUserExists(email)
            }
        }
    }

    private fun checkUserExists(email: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isNotEmpty()) {
                    // User exists, redirect to Main Page
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this, MainPageActivity::class.java));
                } else {
                    // User does not exist, redirect to Registration Page
                    Toast.makeText(this, "Account not found. Redirecting to Registration.", Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this, RegistrationActivity::class.java));
                }
            } else {
                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}