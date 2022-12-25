package com.example.socialapp.models

class expense (
    val createdBy : Users = Users(),
    val mainExpense : String = "",
    val location : String? = "",
    val payMode : String? = "",
    val time : Long = 0L,
    val text : String? ="",
    val likedBy : ArrayList<String> = ArrayList()
        )