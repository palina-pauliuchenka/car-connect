package com.pp272cs388.carconnect.main_screen_dir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pp272cs388.carconnect.R

data class RideHistoryItem(
    val name: String,
    val destination: String,
    val eta: String
)

class RideHistoryFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var rideHistoryRecyclerView: RecyclerView
    private val rideHistoryList = mutableListOf<RideHistoryItem>()
    private lateinit var rideHistoryAdapter: RideHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ride_history, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        rideHistoryRecyclerView = view.findViewById(R.id.rideHistoryRecyclerView)
        rideHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        rideHistoryAdapter = RideHistoryAdapter(rideHistoryList)
        rideHistoryRecyclerView.adapter = rideHistoryAdapter

        fetchRideHistory()

        return view
    }

    private fun fetchRideHistory() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "No user is logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val driveChoice = document.getString("driveChoice") ?: "No"
                    val rideHistory = document.get("rideHistory") as? List<Map<String, Any>>
                    if (rideHistory != null) {
                        rideHistoryList.clear()
                        for (ride in rideHistory) {
                            val destination = ride["destination"] as? String ?: "Unknown Destination"
                            val eta = (ride["ETA"] as? Timestamp).toString() ?: "Unknown ETA"

                            val name = if (driveChoice == "Yes") {
                                ride["pedName"] as? String ?: "Unknown Passenger"
                            } else {
                                ride["driverName"] as? String ?: "Unknown Driver"
                            }

                            val rideHistoryItem = RideHistoryItem(name, destination, eta)
                            rideHistoryList.add(rideHistoryItem)
                        }
                        rideHistoryAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "No ride history available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "User document not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch ride history: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("RideHistoryFragment", "Error fetching ride history", exception)
            }
    }
}
