package com.example.pawalk.models

import com.google.firebase.firestore.GeoPoint

class Location (
    var locate : GeoPoint? = null,
    var name : String = ""
)

{
    constructor() : this(null, ""
    )
}