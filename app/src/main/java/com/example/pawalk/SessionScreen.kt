package com.example.pawalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.pawalk.databinding.ActivitySessionScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SessionScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var biding : ActivitySessionScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = ActivitySessionScreenBinding.inflate(layoutInflater)
        replaceFragment(FeedFragment())
        setContentView(biding.root)

        biding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.toFeed -> {
                    replaceFragment(FeedFragment())
                    true
                }
                R.id.toMap -> {
                    replaceFragment(MapViewFragment())
                    true
                }
                    R.id.toSearch -> {
                        replaceFragment(SearchFragment())
                        true
                    }
                    R.id.toStartSession -> {
                        replaceFragment(AddSessionFragment())
                        true
                    }
                else -> {
                    replaceFragment(ProfileFragment())
                    true
                }
            }

        }

        auth = Firebase.auth
        user = auth.currentUser

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}