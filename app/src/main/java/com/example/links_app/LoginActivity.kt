package com.example.links_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {
                                    val user = doc.toObject(User::class.java)
                                    if (user != null) {
                                        PrefsHelper.saveCurrentUser(this, user)
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this, "User data corrupted", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    val newUser = User(user_id = uid, name = "", username = "", email = email )
                                    db.collection("users").document(uid).set(newUser)
                                    PrefsHelper.saveCurrentUser(this, newUser)
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to load user: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Login failed: ${t.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }