package com.example.donorsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast  // Add this import
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DriveListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BloodDriveAdapter
    private val bloodDriveList = mutableListOf<BloodDriveData>()

    // Firebase variables
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_list)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Setup back button
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupBottomNavigation()
        loadBloodDrivesFromFirebase()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BloodDriveAdapter(bloodDriveList) { bloodDrive ->
            showDriveDetails(bloodDrive)
        }
        recyclerView.adapter = adapter
    }

    private fun loadBloodDrivesFromFirebase() {
        val drivesRef = database.child("bloodDrives")

        drivesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bloodDriveList.clear()

                for (driveSnapshot in snapshot.children) {
                    val bloodDrive = driveSnapshot.getValue(BloodDriveData::class.java)
                    bloodDrive?.let {
                        // Only show active drives
                        if (it.status == "active") {
                            bloodDriveList.add(it)
                        }
                    }
                }

                // Update the adapter
                adapter.notifyDataSetChanged()

                if (bloodDriveList.isEmpty()) {
                    Toast.makeText(this@DriveListActivity, "No blood drives found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DriveListActivity, "Failed to load drives: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("DriveListActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun showDriveDetails(bloodDrive: BloodDriveData) {
        // Show drive details using Toast
        Toast.makeText(
            this,
            "Selected: ${bloodDrive.venue}\nDate: ${bloodDrive.date}\nTime: ${bloodDrive.time}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setupBottomNavigation() {
        try {
            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigation.menu.clear()
            bottomNavigation.inflateMenu(R.menu.org_btm_nav_menu)
            bottomNavigation.selectedItemId = R.id.navigation_events

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        val intent = Intent(this@DriveListActivity, OrgMainActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.navigation_events -> {
                        true
                    }
                    R.id.navigation_profile -> {
                        val intent = Intent(this@DriveListActivity, OrgProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            Log.e("DriveListActivity", "Error setting up bottom navigation: ${e.message}")
        }
    }
}