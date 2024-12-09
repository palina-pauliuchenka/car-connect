package com.pp272cs388.carconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.util.Locale

class login_signup_screen: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput          = findViewById<EditText>(R.id.emailEditText)
        val loginSignupButton   = findViewById<Button>(R.id.loginSignupButton)

        // Validate email, login/register
        loginSignupButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your university email address.", Toast.LENGTH_SHORT).show()
            } else if(!email.endsWith(".edu")) {
                Toast.makeText(this, "Only .edu email addresses are allowed.", Toast.LENGTH_SHORT).show()
            } else {
                // checkUserExists(email)
                checkIfUserExists(email)
            }
        }
    }

    private fun checkIfUserExists(email: String) {
        val dummyPassword = "dummyPassword123" // A fake password just for this check

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, dummyPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // This should not happen because the dummy password is invalid
                    Toast.makeText(this, "Unexpected success. Please try again.", Toast.LENGTH_SHORT).show()
                } else {
                    val exception = task.exception

                    if (exception != null) {
                        exception.printStackTrace() // Log the exception for debugging
                        Toast.makeText(this, "Exception: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }

                    if (exception is FirebaseAuthInvalidUserException) {
                        if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                            // The email is not registered
                            Toast.makeText(this, "Email not registered. Proceed to register.", Toast.LENGTH_SHORT).show()
                            // Redirect to registration screen
                            registerUser(email)
                        }
                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                        if (exception.errorCode == "ERROR_WRONG_PASSWORD") {
                            // The email exists but the password is wrong
                            Toast.makeText(this, "Email exists. Redirecting to login.", Toast.LENGTH_SHORT).show()
                            // Redirect to login screen
                            loginUser(email)
                        }
                    } else {
                        // Handle other errors
                        Toast.makeText(this, "Error: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }



    // Check if email is registered
    private fun checkUserExists(email: String) {
        val dummyPassword = "user-password"

        // Attempt to sign in with a dummy password
        auth.signInWithEmailAndPassword(email, dummyPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Unexpected success. Please try again.", Toast.LENGTH_SHORT).show()
                } else {
                    when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // User not found, proceed to registration
                            Toast.makeText(this, "Registering", Toast.LENGTH_SHORT).show()
                            registerUser(email)
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // User exists but wrong password
                            if (exception.errorCode == "ERROR_WRONG_PASSWORD") {
                                Toast.makeText(this, "Login in", Toast.LENGTH_SHORT).show()
                                loginUser(email)
                            }
                        }
                        else -> {
                            Toast.makeText(this, "Error: ${exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun registerUser(email: String) {
        Toast.makeText(this, "Account not found. Redirecting to Registration.", Toast.LENGTH_SHORT).show()
        val password = "user-password" // Replace afterwards

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        Toast.makeText(this, "Verification email sent.", Toast.LENGTH_SHORT).show()

                        // Monitor email verification
                        monitorEmailVerification()
                    } else {
                        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Registration Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String) {
        // Prompt for password and authenticate the user
        val password = "user-password"
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
            task ->
            if (task.isSuccessful) {
                // Unexpected success -> handle accordingly
                // Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this, main_screen::class.java))
            } else {
                // Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                val exception = task.exception
                if (exception is FirebaseAuthInvalidUserException) {
                    if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                        // Email is not registered
                        registerUser(email)
                        Log.d("MainActivity:", exception.errorCode)
                    }
                } else if(exception is FirebaseAuthInvalidCredentialsException) {
                    if (exception.errorCode == "ERROR_WRONG_PASSWORD") {
                        // Move to next page
                        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, main_screen::class.java))
                    }
                }
            }
        }
    }

    // Monitor Email Verification
    private fun monitorEmailVerification() {
        val user = auth.currentUser

        if (user != null) {
            // Redirect to form after verification
            // val intent = Intent(this, registration_screen::class.java)
            Toast.makeText(this, "Redirecting to form.", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, registration_screen::class.java)
            // startActivity(intent)
        } else {
            Toast.makeText(this, "Email verification not complete. Check your inbox.", Toast.LENGTH_SHORT).show()
        }
    }
}