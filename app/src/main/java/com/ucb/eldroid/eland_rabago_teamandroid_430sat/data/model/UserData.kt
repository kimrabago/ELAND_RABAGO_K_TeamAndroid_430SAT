package com.ucb.eldroid.eland_teamandroid_430sat.data.model

data class UserData(
    var userID: Int = 0,
    var username: String,
    var email: String,
    var password: String,
    val isVerified: Boolean = false,
    val profileImage: String? = null
)