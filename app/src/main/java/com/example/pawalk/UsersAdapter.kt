package com.example.pawalk

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pawalk.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UsersAdapter (val context : Context, val users: List<User>) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    var auth: FirebaseAuth = Firebase.auth
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var signedInUserEmail: String? = auth.currentUser?.email

    override fun getItemCount() = users.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersAdapter.ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            firestore.collection("users")
                .document(user?.email as String)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    var signedInUser = userSnapshot.toObject(User::class.java)
                    Glide.with(context).load(signedInUser?.profilePicUri).into(itemView.findViewById(R.id.avatar_image_view))
                    itemView.findViewById<TextView>(R.id.display_name_text_view).text = signedInUser?.username
                }
        }
    }
}