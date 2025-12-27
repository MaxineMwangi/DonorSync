package com.example.donorsync

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView

class OrgProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var editImageButton: ImageButton
    private lateinit var saveButton: Button

    // Activity result launcher for image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { imageUri ->
                profileImage.setImageURI(imageUri)
                Toast.makeText(this, "Profile image updated!", Toast.LENGTH_SHORT).show()

                // Save the image URI for persistence
                val sharedPref = getSharedPreferences("org_profile", MODE_PRIVATE)
                sharedPref.edit().putString("profile_image_uri", imageUri.toString()).apply()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_profile)

        initializeViews()
        setupBottomNavigation()
        setupClickListeners()
        loadProfileInformation()
    }

    private fun initializeViews() {
        profileImage = findViewById(R.id.profile_image)
        editImageButton = findViewById(R.id.edit_image_button)
        saveButton = findViewById(R.id.save_button)
    }

    private fun setupClickListeners() {
        // Edit profile image
        editImageButton.setOnClickListener {
            openGallery()
        }

        // Also allow clicking on the main profile image
        profileImage.setOnClickListener {
            openGallery()
        }

        // Save profile information
        saveButton.setOnClickListener {
            saveProfileInformation()
        }

        // Change Password
        findViewById<CardView>(R.id.change_password_card).setOnClickListener {
            showChangePasswordDialog()
        }

        // Notification Settings
        findViewById<CardView>(R.id.notification_settings_card).setOnClickListener {
            showNotificationSettings()
        }

        // Privacy Settings
        findViewById<CardView>(R.id.privacy_settings_card).setOnClickListener {
            showPrivacySettings()
        }

        // Help & Support
        findViewById<CardView>(R.id.help_support_card).setOnClickListener {
            showHelpSupport()
        }

        // Delete Account
        findViewById<CardView>(R.id.delete_account_card).setOnClickListener {
            showDeleteAccountConfirmation()
        }

        // Logout
        findViewById<CardView>(R.id.logout_card).setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        imagePickerLauncher.launch(galleryIntent)
    }

    private fun saveProfileInformation() {
        val orgName = findViewById<TextInputEditText>(R.id.org_name_input).text.toString()
        val email = findViewById<TextInputEditText>(R.id.email_input).text.toString()
        val phone = findViewById<TextInputEditText>(R.id.phone_input).text.toString()
        val address = findViewById<TextInputEditText>(R.id.address_input).text.toString()

        if (orgName.isNotEmpty() && email.isNotEmpty()) {
            val sharedPref = getSharedPreferences("org_profile", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("org_name", orgName)
                putString("email", email)
                putString("phone", phone)
                putString("address", address)
                apply()
            }

            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please fill in organization name and email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileInformation() {
        val sharedPref = getSharedPreferences("org_profile", MODE_PRIVATE)

        // Load text fields
        findViewById<TextInputEditText>(R.id.org_name_input).setText(
            sharedPref.getString("org_name", "Blood Donation Center")
        )
        findViewById<TextInputEditText>(R.id.email_input).setText(
            sharedPref.getString("email", "contact@bloodcenter.org")
        )
        findViewById<TextInputEditText>(R.id.phone_input).setText(
            sharedPref.getString("phone", "+1 (555) 123-4567")
        )
        findViewById<TextInputEditText>(R.id.address_input).setText(
            sharedPref.getString("address", "123 Medical Center Drive, City, State 12345")
        )

        // Load profile image if saved
        val imageUriString = sharedPref.getString("profile_image_uri", null)
        imageUriString?.let {
            profileImage.setImageURI(Uri.parse(it))
        }
    }

    private fun showChangePasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage("Password change functionality will be implemented here.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showNotificationSettings() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Notification Settings")
            .setMessage("Configure your notification preferences here.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showPrivacySettings() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Privacy Settings")
            .setMessage("Manage your privacy preferences here.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showHelpSupport() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Help & Support")
            .setMessage("Contact support or view help documentation.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showDeleteAccountConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { dialog, which ->
                // Implement account deletion logic
                Toast.makeText(this, "Account deletion would be processed here", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { dialog, which ->
                // Implement logout logic
                val intent = Intent(this, OrgMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_profile

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@OrgProfileActivity, OrgMainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_events -> {
                    val intent = Intent(this@OrgProfileActivity, DriveListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }
    }
}