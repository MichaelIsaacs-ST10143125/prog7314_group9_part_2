package com.example.links_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.links_app.R
import com.example.links_app.models.ChatPreview

class ChatPreviewAdapter(
    private val chatList: List<ChatPreview>,
    private val onClick: (ChatPreview) -> Unit
) : RecyclerView.Adapter<ChatPreviewAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.chat_user_name)
        val lastMessageText: TextView = itemView.findViewById(R.id.chat_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_preview, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.nameText.text = chat.userName
        holder.lastMessageText.text = chat.lastMessage
        holder.itemView.setOnClickListener { onClick(chat) }
    }

    override fun getItemCount() = chatList.size
}
