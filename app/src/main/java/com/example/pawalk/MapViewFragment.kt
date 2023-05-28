package com.example.pawalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pawalk.models.Location
import com.example.pawalk.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var locations: MutableList<Location>
    private lateinit var Bucharest : GeoPoint


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        locations = mutableListOf()
        Bucharest = GeoPoint(44.4274679459006, 26.09805559857452)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_map_view, container, false)
        firestore = FirebaseFirestore.getInstance()

        val mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        // Async map
        mapFragment.getMapAsync(OnMapReadyCallback { googleMap ->
            // When map is loaded
            //show pet-friendly locations
            firestore.collection("locations").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document != null) {
                            val geoPoint: GeoPoint? = document.getGeoPoint("locate")
                            val name: String? = document.getString("name")
                            val location = geoPoint?.let { LatLng(geoPoint.latitude, geoPoint.longitude) }
                            googleMap.addMarker(MarkerOptions().position(location!!).title(name))
                        }
                    }
                } else {
                    Log.d("TAG", task.exception!!.message!!) //Never ignore potential errors!
                }
            }

            //show friends locations
            firestore.collection("posts").whereGreaterThan("expirationTime", System.currentTimeMillis()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val geoPoint: GeoPoint? = document.getGeoPoint("geoLocation")
                        val user: HashMap<String, String> = document.get("user") as HashMap<String, String>
                        val location = geoPoint?.let {LatLng(geoPoint.latitude, geoPoint.longitude)}
                        val username = user["username"]
                        googleMap.addMarker(MarkerOptions().position(location!!).title(username).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    }
                } else {
                    Log.d("TAG", task.exception!!.message!!) //Never ignore potential errors!
                }
            }

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(50F), 400, null);

        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}