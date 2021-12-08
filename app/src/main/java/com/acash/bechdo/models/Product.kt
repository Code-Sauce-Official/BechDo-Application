package com.acash.bechdo.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

class Product(
    val productId:String,
    val postedBy:String,
    val title:String,
    val titleLowerCase:String,
    val description:String,
    val price:Long,
    val downLoadUrlsPics:ArrayList<String>,
    val category:String,
    val forRent:Boolean,
    val priceFilterIndex:Long,
    val clg:String,
    @field:JvmField
    var isActive:Boolean=true,
    @ServerTimestamp
    val createdDate:Date?=null
) {
    constructor():this("","","","","",0L,ArrayList<String>(),"",false,0L,"",true,null)
}