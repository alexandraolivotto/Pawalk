package com.example.pawalk.models

import com.google.firebase.firestore.GeoPoint

data class Post (
    var caption : String = "",
    var imageUri: String = "",
    var duration: String = "",
    var location: String = "",
    var creationTimeMs: Long = 0,
    var user: User? = null,
    var paws: Int = 0,
    var geoLocation: GeoPoint? = null
)
{
    constructor() : this( "", "","", "", 0, null, 0, null
    )
}