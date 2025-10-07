package com.example.links_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.links_app.adapters.ChatPreviewAdapter
import com.example.links_app.models.ChatPreview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class HomeFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchedUserLayout: ConstraintLayout
    private lateinit var searchedName: TextView
    private lateinit var recyclerViewChats: RecyclerView
    private lateinit var adapter: ChatPreviewAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val chatList = mutableListOf<ChatPreview>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val settings = FirebaseFirestoreSettings.Builder()
            .build()
        db.firestoreSettings = settings

        searchView = view.findViewById(R.id.search_view)
        searchedUserLayout = view.findViewById(R.id.searched_user)
        searchedName = view.findViewById(R.id.searched_name)
        recyclerViewChats = view.findViewById(R.id.recyclerViewChats)

        searchedUserLayout.visibility = View.GONE

        // RecyclerView setup
        recyclerViewChats.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatPreviewAdapter(chatList) { selectedChat ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("user_id", selectedChat.userId)
            intent.putExtra("user_name", selectedChat.userName)
            startActivity(intent)
        }
        recyclerViewChats.adapter = adapter

        setupSearch()
        loadChatPreviews()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadChatPreviews() // reload chats every time fragment comes back
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchUser(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
    }

    private fun searchUser(name: String) {
        db.collection("users")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    searchedUserLayout.visibility = View.GONE
                    Toast.makeText(requireContext(), "No user found", Toast.LENGTH_SHORT).show()
                } else {
                    val userDoc = result.documents.first()
                    val userId = userDoc.getString("user_id") ?: ""
                    val userName = userDoc.getString("name") ?: ""

                    searchedUserLayout.visibility = View.VISIBLE
                    searchedName.text = userName

                    searchedUserLayout.setOnClickListener {
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra("user_id", userId)
                        intent.putExtra("user_name", userName)
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadChatPreviews() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error loading chats", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots == null) return@addSnapshotListener

                val updatedChats = mutableListOf<ChatPreview>()

                for (doc in snapshots.documents) {
                    val chatId = doc.id
                    val participants = (doc.get("participants") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                    val lastMessage = doc.getString("lastMessage") ?: ""
                    val timestamp = doc.getLong("lastTimestamp") ?: 0L

                    val otherUserId = participants.firstOrNull { it != currentUserId } ?: continue

                    db.collection("users").document(otherUserId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val userName = userDoc.getString("name") ?: "Unknown"
                            val preview = ChatPreview(chatId, otherUserId, userName, lastMessage, timestamp)

                            // Prevent duplicates
                            updatedChats.removeAll { it.chatId == chatId }
                            updatedChats.add(preview)

                            // Sort by latest message
                            updatedChats.sortByDescending { it.timestamp }

                            chatList.clear()
                            chatList.addAll(updatedChats)
                            adapter.notifyDataSetChanged()
                        }
                }
            }
    }
}
