package com.example.pawalk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pawalk.models.Post
import com.example.pawalk.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firestore.v1.WriteResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firestore: FirebaseFirestore
    private var signedInUser: User? = null
    private val CAMERA_REQUEST = 1888
    private lateinit var image : ImageView
    private lateinit var storageReference : StorageReference
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_profile_view, container, false)

        image = view.findViewById(R.id.imageView6)
        val logoutButton : Button = view.findViewById(R.id.logoutButton)
        val editUsername: TextInputEditText = view.findViewById(R.id.editUsername)
        val saveButton : Button = view.findViewById(R.id.saveButton)
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                if (signedInUser?.profilePicUri != "") {
                    Glide.with(this.requireContext()).load(signedInUser?.profilePicUri).into(view.findViewById(R.id.imageView6))
                }
                view.findViewById<TextView>(R.id.username_input).text = signedInUser?.username
            }

        image.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }

        saveButton.setOnClickListener {
            val email = FirebaseAuth.getInstance().currentUser?.email as String
            val docRef = firestore.collection("users").document(email)
            docRef.update("username", editUsername.text.toString())
            editUsername.text?.clear()
            Toast.makeText(activity, "Username updated!", Toast.LENGTH_SHORT).show()

            val photoRef = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
            photoRef.putFile(photoUri!!)
                .continueWithTask {
                    photoRef.downloadUrl
                }
                .continueWithTask { downloadUrlTask ->
                   docRef.update("profilePicUri", downloadUrlTask.result.toString())
                }.addOnCompleteListener { postCreationTask ->
                    if (!postCreationTask.isSuccessful) {
                        Toast.makeText(activity, "Profile picture update failed", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(activity, "Profile picture successfully updated!", Toast.LENGTH_SHORT).show()
                }
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}