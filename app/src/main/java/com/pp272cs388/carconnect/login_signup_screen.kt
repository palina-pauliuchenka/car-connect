package com.pp272cs388.carconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class login_signup_screen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Initialize Views
        val emailInput = findViewById<EditText>(R.id.emailEditText)
        val passwordInput = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signupButton)

        // Login Button Click Listener
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        // Sign-Up Button Click Listener
        signUpButton.setOnClickListener {
             signUpUser()
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

    // Login Existing User
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Login successful! Welcome!", Toast.LENGTH_SHORT).show()
                        // Redirect to Main Page or Dashboard

                        startActivity(Intent(this, main_screen::class.java))
                    } else {
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Sign-Up New User
    private fun signUpUser() {
        startActivity(Intent(this, registration_screen::class.java))
    }
}
