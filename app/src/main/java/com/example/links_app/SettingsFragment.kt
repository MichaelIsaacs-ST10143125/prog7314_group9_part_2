package com.example.links_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var editButton: MaterialButton
    private lateinit var logoutButton: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Find views
        usernameTextView = view.findViewById(R.id.textView2)
        bioTextView = view.findViewById(R.id.bioTextView)
        editButton = view.findViewById(R.id.editButton)
        logoutButton = view.findViewById(R.id.logout_button)

        // Load current user info
        loadCurrentUserName()
        loadCurrentUserBio()

        // Click listeners
        editButton.setOnClickListener { enableBioEditing() }
        logoutButton.setOnClickListener { logoutUser() }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadCurrentUserName() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("name") ?: "Username"
                    usernameTextView.text = username
                }
            }
            .addOnFailureListener {
                usernameTextView.text = "Username"
            }
    }

    private fun loadCurrentUserBio() {
        val currentUserId = auth.currentUser?.uid ?: return
        db.collection("users").document(currentUserId)
            .get()
            .addOnSuccessListener { doc ->
                val bio = doc.getString("bio") ?: "Set your bio..."
                bioTextView.text = bio
            }
    }

    // ------------------- Bio Editing -------------------
    @SuppressLint("SetTextI18n")
    private fun enableBioEditing() {
        val parent = bioTextView.parent as ViewGroup
        val index = parent.indexOfChild(bioTextView)
        parent.removeView(bioTextView)

        val bioEditText = EditText(requireContext()).apply {
            setText(bioTextView.text)
            textSize = 20f
            layoutParams = bioTextView.layoutParams
        }

        parent.addView(bioEditText, index)

        editButton.text = "Save"
        editButton.setOnClickListener {
            val newBio = bioEditText.text.toString().trim()
            saveBioToFirestore(newBio)

            parent.removeView(bioEditText)
            bioTextView.text = newBio
            parent.addView(bioTextView, index)

            editButton.text = "Edit Profile"
            editButton.setOnClickListener { enableBioEditing() }
        }
    }

    private fun saveBioToFirestore(bio: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        db.collection("users").document(currentUserId)
            .update("bio", bio)
    }

    // ------------------- Logout -------------------
    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
