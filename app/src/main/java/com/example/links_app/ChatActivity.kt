package com.example.links_app

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.links_app.adapters.MessagesAdapter
import com.example.links_app.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    // Adapter for RecyclerView displaying messages
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageView

    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // User and chat identifiers
    private var currentUserId: String = ""
    private var otherUserId: String = ""
    private var otherUserName: String = ""
    private var chatId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Back button closes activity
        findViewById<ImageView>(R.id.back_to_home_icon).setOnClickListener {
            finish()
        }

        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Correctly set current user as logged-in user
        currentUserId = auth.currentUser?.uid ?: ""
        otherUserId = intent.getStringExtra("user_id") ?: ""
        otherUserName = intent.getStringExtra("user_name") ?: ""

        findViewById<TextView>(R.id.contact_name).text = otherUserName

        // Generate consistent chatId
        chatId = if (currentUserId < otherUserId) {
            "${currentUserId}_$otherUserId"
        } else {
            "${otherUserId}_$currentUserId"
        }

        // Setup RecyclerView and Adapter
        messagesAdapter = MessagesAdapter(currentUserId)
        rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply { stackFromEnd = true }
            adapter = messagesAdapter
        }

        // Listen for messages in real-time
        listenForMessages()

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                etMessage.text.clear()
            }
        }
    }

    // Function to send a message to Firestore
    private fun sendMessage(text: String) {
        val message = Message(
            text = text,
            senderId = currentUserId,
            timestamp = System.currentTimeMillis()
        )

        db.collection("chats").document(chatId)
            .collection("messages")
            .add(message)
    }

    // Function to listen for messages in real-time and update RecyclerView
    private fun listenForMessages() {
        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val msgs = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    messagesAdapter.submitList(msgs)
                    rvMessages.scrollToPosition(msgs.size - 1)
                }
            }
    }
}
