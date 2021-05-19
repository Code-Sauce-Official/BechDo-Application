package com.acash.bechdo.models

class User(
    val uid:String,
    var name:String,
    val dob:String,
    val clg:String,
    var year:String,
    val downloadUrlClgId: String,
    var downloadUrlDp:String,
    @field:JvmField
    val isAccountActivated:Boolean=true,
    ){
    constructor():this("","","","","","","",true)
}