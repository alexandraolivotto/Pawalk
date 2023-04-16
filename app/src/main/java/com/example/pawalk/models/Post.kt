package com.example.pawalk.models

data class Post (
    var caption : String = "",
    var imageUri: String = "",
    var duration: String = "",
    var location: String = "",
    var creationTimeMs: Long = 0,
    var user: User? = null
        )

{
    constructor() : this( "", "","", "", 0, null
    )
}