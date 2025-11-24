package com.example.donorsync

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrgMainActivity : AppCompatActivity() {

    private val TAG = "OrgMainActivity"
    private val bloodDriveList = mutableListOf<BloodDriveData>()
    private val backgroundHandler = Handler(Looper.getMainLooper())
    private lateinit var backgroundRunnable: Runnable

    // Background images array
    private val backgroundImages = listOf(
        R.drawable.blood_drive_bg1,
        R.drawable.blood_drive_bg2,
        R.drawable.blood_drive_bg3,
        R.drawable.blood_drive_bg4,
        R.drawable.blood_drive_bg5
    )

    private val postDriveLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val bloodDrive = data?.getSerializableExtra("bloodDriveData") as? BloodDriveData
            bloodDrive?.let {
                bloodDriveList.add(it)
                Toast.makeText(this, "Blood drive added!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_main)

        try {
            setupSlideshow()
        } catch (e: Exception) {
            Log.e(TAG, "Slideshow setup failed, continuing without it: ${e.message}")
        }

        setupButtons()
        setupBottomNavigation()
    }

    private fun setupSlideshow() {
        try {
            val backgroundViewPager = findViewById<ViewPager2>(R.id.backgroundViewPager)

            if (backgroundViewPager == null) {
                Log.d(TAG, "No ViewPager2 found in layout - slideshow disabled")
                return
            }

            val adapter = BackgroundSliderAdapter(backgroundImages)
            backgroundViewPager.adapter = adapter

            // Initialize the runnable
            backgroundRunnable = Runnable {
                try {
                    val currentItem = backgroundViewPager.currentItem
                    val nextItem = (currentItem + 1) % backgroundImages.size
                    backgroundViewPager.currentItem = nextItem
                } catch (e: Exception) {
                    Log.e(TAG, "Error in slideshow animation: ${e.message}")
                }
            }

            // Auto-scroll every 4 seconds
            backgroundViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    backgroundHandler.removeCallbacks(backgroundRunnable)
                    backgroundHandler.postDelayed(backgroundRunnable, 4000)
                }
            })

            // Start auto-scrolling
            backgroundHandler.postDelayed(backgroundRunnable, 4000)

        } catch (e: Exception) {
            Log.e(TAG, "Error setting up slideshow: ${e.message}")
        }
    }

    private fun setupButtons() {
        try {
            // Setup Post Drive button
            val postDriveButton = findViewById<Button>(R.id.btn_post_drive)
            postDriveButton.setOnClickListener {
                val intent = Intent(this, PostDriveActivity::class.java)
                postDriveLauncher.launch(intent)
            }

            // Setup View Drives button
            val viewDrivesButton = findViewById<Button>(R.id.btn_view_drives)
            viewDrivesButton.setOnClickListener {
                val intent = Intent(this, DriveListActivity::class.java).apply {
                    putExtra("bloodDriveList", ArrayList(bloodDriveList))
                }
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up buttons: ${e.message}")
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_home // Highlight Home as current

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home, do nothing or refresh
                    true
                }
                R.id.navigation_events -> {
                    // Navigate to events (DriveListActivity)
                    val intent = Intent(this, DriveListActivity::class.java).apply {
                        putExtra("bloodDriveList", ArrayList(bloodDriveList))
                    }
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Navigate to profile (we'll create this)
                    val intent = Intent(this, OrgProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::backgroundRunnable.isInitialized) {
            backgroundHandler.removeCallbacks(backgroundRunnable)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reset home as selected when returning to this activity
        findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.navigation_home

        if (::backgroundRunnable.isInitialized) {
            backgroundHandler.postDelayed(backgroundRunnable, 4000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::backgroundRunnable.isInitialized) {
            backgroundHandler.removeCallbacks(backgroundRunnable)
        }
    }
}