package com.acash.bechdo.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

class Product(
    val postedBy:String,
    val title:String,
    val titleLowerCase:String,
    val description:String,
    val price:String,
    val downLoadUrlsPics:ArrayList<String>,
    val tags:ArrayList<String>,
    val forRent:Boolean,
    @field:JvmField
    val isActive:Boolean=true,
    @ServerTimestamp
    val createdDate:Date?=null
) {
    constructor():this("","","","","",ArrayList<String>(),ArrayList<String>(),false,true,null)
}