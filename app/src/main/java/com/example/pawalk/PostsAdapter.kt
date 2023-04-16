package com.example.pawalk

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pawalk.models.Post


class PostsAdapter (val context : Context, val posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {


    override fun getItemCount() = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {
            itemView.findViewById<TextView>(R.id.display_name_text_view).text = post.user?.email
            itemView.findViewById<TextView>(R.id.post_time_text_view).text = post.location + " | " + post.duration + " | " + DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
            //itemView.findViewById<ImageView>(R.id.item_gallery_post_image_imageview).setImageURI(Uri.parse(post.imageUri))
            Glide.with(context).load(post.imageUri).into(itemView.findViewById(R.id.item_gallery_post_image_imageview))
        }
    }
}