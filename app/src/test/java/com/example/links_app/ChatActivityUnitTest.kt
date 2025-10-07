package com.example.links_app

import com.example.links_app.models.Message
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ChatActivityUnitTest {

    private lateinit var mockDb: FirebaseFirestore
    private lateinit var mockChatCollection: CollectionReference
    private lateinit var mockMessagesCollection: CollectionReference
    private lateinit var mockDocRef: DocumentReference
    private lateinit var activity: ChatActivity

    @Before
    fun setup() {
        mockDb = mock(FirebaseFirestore::class.java)
        mockChatCollection = mock(CollectionReference::class.java)
        mockMessagesCollection = mock(CollectionReference::class.java)
        mockDocRef = mock(DocumentReference::class.java)

        // ✅ Properly build the activity with Robolectric
        activity = Robolectric.buildActivity(ChatActivity::class.java).setup().get()

        // Inject mocks into private fields
        activity.apply {
            val dbField = ChatActivity::class.java.getDeclaredField("db")
            dbField.isAccessible = true
            dbField.set(this, mockDb)

            val currentUserField = ChatActivity::class.java.getDeclaredField("currentUserId")
            currentUserField.isAccessible = true
            currentUserField.set(this, "userA")

            val chatIdField = ChatActivity::class.java.getDeclaredField("chatId")
            chatIdField.isAccessible = true
            chatIdField.set(this, "userA_userB")
        }

        // Mock Firestore behavior
        `when`(mockDb.collection("chats")).thenReturn(mockChatCollection)
        `when`(mockChatCollection.document("userA_userB")).thenReturn(mockDocRef)
        `when`(mockDocRef.collection("messages")).thenReturn(mockMessagesCollection)
    }

    @Test
    fun `sendMessage should add message to firestore`() {
        val text = "Hello world!"
        val method = ChatActivity::class.java.getDeclaredMethod("sendMessage", String::class.java)
        method.isAccessible = true
        method.invoke(activity, text)

        // ✅ Verify that a message was added
        verify(mockMessagesCollection, times(1)).add(any(Message::class.java))
    }
}
