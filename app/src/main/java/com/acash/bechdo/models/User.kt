package com.acash.bechdo.models

class User(
    val uid:String,
    val name:String,
    val dob:String,
    val clg:String,
    val year:String,
    val downloadUrlClgId: String,
    val downloadUrlDp:String
    ){
    constructor():this("","","","","","","")
}