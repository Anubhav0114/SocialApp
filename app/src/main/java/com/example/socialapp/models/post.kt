package com.example.socialapp.models

class post (
    val text : String = "",
    val createdBy : Users = Users(),
    val time : Long = 0L,
    val imgUrl : String = "",
    val likedBy : ArrayList<String> = ArrayList()

  )