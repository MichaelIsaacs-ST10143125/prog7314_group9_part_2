Links

Overview

Links App is a real-time chat application built for Android using Kotlin, Firebase Authentication aand Firestore but will later use a created REST API and database. The app allows users to create an account, log in, and communicate with other users in real-time. It also supports profile customization such as bio editing, and secure authentication.

The app is designed to be intuitive, responsive, and simple to use, offering a smooth messaging experience with features like sending and receiving messages, dynamic message backgrounds, and user profile management.


1. Features include:

- User authentication with Firebase (Sign Up & Sign In)
- Real-time chat with other users
- Dynamic message UI 
- User profile management (view & edit bio)
- Logout functionality
- Firebase Firestore database for storing users and messages

2. App Workflow

The app follows a standard navigation flow:

1. Splash Screen
   - Displays the app logo while loading necessary data.
   - Redirects to the authentication flow depending on user status.

2. Login Screen
   - Existing users enter their email and password.
   - Successful login redirects to the **HomeFragment**.
   - Option to navigate to **Register Screen** for new users.

3. Register Screen
   - New users provide their name, username, email and password
   - User data is stored in Firebase Authentication and Firestore.
   - Successful registration redirects to the Login Screen for sign-in.

4. Home Screen
   - Allows user's to search for other users in the database
   - Users can click on a contact to open a ChatActivity

5. ChatActivity
   - Shows conversation with selected user.
   - Sent messages are displayed in a colored bubble (green by default).
   - Received messages are displayed in a different colored bubble (gray by default).
   - Users can type messages in an EditText and send using a send button.
   - Back button allows users to return to the HomeFragment.

6. SettingsFragment
   - Users can view and edit their bio.
   - Option to logout, which returns them to the **Login Screen**.

---

Project Structure

- MainActivity – Hosts fragments such as HomeFragment and SettingsFragment.
- ChatActivit` – Handles chat functionality and message display.
- SettingsFragment – Manages user profile and logout functionality.
- Adapters – RecyclerView adapters for chat messages.
- Models – Data classes like Message for structured Firestore storage.
- Firebase – Handles authentication, database, and storage integration.

---

Dependencies

- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Material Design Components
- RecyclerView
- Kotlin Coroutines
- Robolectric and Mockito for unit testing

---

Testing

Automated unit tests are implemented for key functionalities such as sending messages. Tests use **Mockito** for mocking Firebase dependencies and **Robolectric** for running Android tests in the JVM environment. GitHub Actions is configured to build the app and run tests on every push or pull request.

---

Running the App

1. Launch the App:
   - Open the project in Android Studio.
   - Connect an Android device or start an emulator.
   - Press **Run** (green play button) to install and launch the app.

2. App Flow:

   - Splash Screen:
     When the app starts, you’ll see a splash screen with the app logo.

   - Login Screen:  
     After the splash screen, users are taken to the Login screen.  
     - Enter your email and password to sign in.  
     - If you don’t have an account, tap Sign Up to create one.

   - Registration Screen:  
     - Enter your name, email, and password.    
     - After successful registration, you’ll be redirected to the Login screen.

   - Home Screen:
     - Once logged in, you land on the **Home** screen.  
     - Here you can search for contacts, navigate to other app pages.  

   - Chat Functionality:  
     - Tap a contact to open a chat.  
     - Send messages using the input field and send button.  
     - Messages are synced in real-time using Firebase Firestore.  
     - Tap the back icon to return to the Home screen.

   - Settings / Profile:  
     - Access your profile from the Settings screen.  
     - Edit your bio.  
     - Log out using the Logout button, which returns you to the Login screen.

3. Notes:  
   - Ensure you have an active internet connection for Firebase functionality.  
   - The app uses Firebase Authentication for login and Firestore for message storage.  
   - The app is compatible with Android SDK 33 and above.
