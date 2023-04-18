package com.example.pawalk

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.pawalk.models.Post
import com.example.pawalk.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostSession.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostSession : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    private var signedInUser : User? = null
    private val CAMERA_REQUEST = 1888

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference : StorageReference
    private lateinit var location : TextInputEditText
    private lateinit var duration : TextInputEditText
    private lateinit var caption : TextInputEditText
    private lateinit var image : ImageView
    private lateinit var discardButton : FloatingActionButton
    private lateinit var postButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        // Inflate the layout for this fragment
        val view : View =  inflater.inflate(R.layout.fragment_post_session, container, false)

        location = view.findViewById(R.id.locationInput)
        duration = view.findViewById(R.id.durationInput)
        caption = view.findViewById(R.id.caption)
        image = view.findViewById(R.id.imageUpload)
        discardButton = view.findViewById(R.id.discardButton)
        postButton = view.findViewById(R.id.postButton)

        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
            }

        image.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }

        postButton.setOnClickListener {
            handlePostButton()
            //redirect
        }

        discardButton.setOnClickListener {
            caption.text?.clear()
            image.setImageResource(R.drawable.ic_camera_foreground)
            duration.text?.clear()
            location.text?.clear()
            Toast.makeText(activity, "Session cleared!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun getImageUri(inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("temprentpk", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!.get("data")  as Bitmap?
            photoUri = imageBitmap?.let { getImageUri(it) }
            Log.d("imageURI", "" + photoUri)
            image.setImageBitmap(imageBitmap)
            image.setImageURI(photoUri)
        }
    }


    private fun handlePostButton() {
        if (location.text!!.isBlank()) {
            Toast.makeText(activity, "Location not set!", Toast.LENGTH_SHORT).show()
            return
        }
        if (duration.text!!.isBlank()) {
            Toast.makeText(activity, "Duration not set!", Toast.LENGTH_SHORT).show()
            return
        }
        if (caption.text!!.isBlank()) {
            Toast.makeText(activity, "Caption not set!", Toast.LENGTH_SHORT).show()
            return
        }
        if (photoUri == null) {
            Toast.makeText(activity, "Image not uploaded!", Toast.LENGTH_SHORT).show()
            return
        }

        val photoRef = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        photoRef.putFile(photoUri!!)
            .continueWithTask {
                photoRef.downloadUrl
            }
            .continueWithTask { downloadUrlTask ->
                val post = Post(caption.text.toString(), downloadUrlTask.result.toString(), duration.text.toString(), location.text.toString(), System.currentTimeMillis(), signedInUser)
                firestore.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->
                if (!postCreationTask.isSuccessful) {
                    Toast.makeText(activity, "Session creation failed", Toast.LENGTH_SHORT).show()
                }
                caption.text?.clear()
                //image.setImageResource(0)
                //image.setImageResource(R.drawable.ic_menu_camera)
                image.setImageResource(R.drawable.ic_camera_foreground)
                duration.text?.clear()
                location.text?.clear()
                Toast.makeText(activity, "Session posted!", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostSession.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostSession().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}