package com.example.pawalk.models

data class User(var email: String, var username: String = "", var bio: String = "")

{
    constructor() : this( "", "","")
}