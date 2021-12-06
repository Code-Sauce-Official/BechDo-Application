package com.acash.bechdo.models

class User(
    val uid: String,
    var name: String,
    val dob: String,
    val clg: String,
    var year: String,
    var downloadUrlDp: String,
    val favouriteProducts:ArrayList<String> = ArrayList(),
) {
    constructor() : this("", "", "", "", "", "", ArrayList<String>())
}