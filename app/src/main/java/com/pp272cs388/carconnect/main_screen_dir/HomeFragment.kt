package com.pp272cs388.carconnect.main_screen_dir

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pp272cs388.carconnect.R
import com.pp272cs388.carconnect.main_screen
import com.pp272cs388.carconnect.schedule_screen

class HomeFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val findRideButton = view.findViewById<Button>(R.id.findRideButton)
        val findPassengerButton = view.findViewById<Button>(R.id.findPassengerButton)
        val statusText = view.findViewById<TextView>(R.id.statusText)

        val userId = auth.currentUser?.uid ?: return view

        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                val driveChoice = document.getString("driveChoice") ?: "No"
                if (driveChoice == "Yes") {
                    findPassengerButton.visibility = View.VISIBLE
                    statusText.text = "Feeling kind? Let's find you someone to drive home!"
                } else {
                    findPassengerButton.visibility = View.GONE
                }
            }
        }

        findRideButton.setOnClickListener {
            val intent = Intent(requireContext(), schedule_screen::class.java)

            // Pass parameters using putExtra (key-value pairs)
            intent.putExtra("PARAM_KEY", "Pedestrian")

            startActivity(intent)
        }
        findPassengerButton.setOnClickListener {
            val intent = Intent(requireContext(), schedule_screen::class.java)

            // Pass parameters using putExtra (key-value pairs)
            intent.putExtra("PARAM_KEY", "Driver")

            startActivity(intent)
        }

        return view
    }
}
