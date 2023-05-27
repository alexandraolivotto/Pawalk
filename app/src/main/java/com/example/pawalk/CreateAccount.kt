package com.example.pawalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.pawalk.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateAccount : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        val editTextEmail : TextInputEditText = findViewById(R.id.email_sign_up_input)
        val editTextPassword : TextInputEditText = findViewById(R.id.password_sign_up_input)
        val editTextUsername : TextInputEditText = findViewById(R.id.username_input)

        val signUpButton : Button = findViewById(R.id.sign_up_button)

        val progressBar : ProgressBar = findViewById(R.id.progressBar)


        signUpButton.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            progressBar.visibility = View.VISIBLE

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Please enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Please enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "createUserWithEmail:success")
                        //val user = auth.currentUser
                       // updateUI(user)
                        progressBar.visibility = View.GONE
                        //link user to users db
                        val newUser : User = User(email, username, "insert bio")
                        val newUserUid = auth.currentUser?.uid
                        if (newUserUid != null) {
                            firestore.collection("users").document(email).set(newUser)
                        }

                        Toast.makeText(baseContext, "Account created.",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                       // updateUI(null)
                    }
                }
        }
    }
    companion object {
        private const val TAG = "EmailPassword"
    }

}