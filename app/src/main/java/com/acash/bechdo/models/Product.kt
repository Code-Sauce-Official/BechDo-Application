package com.acash.bechdo.models

class Product(
    val postedBy:String,
    val title:String,
    val description:String,
    val price:String,
    val downLoadUrlsPics:ArrayList<String>,
    val tag:String
) {
    constructor():this("","","","",ArrayList<String>(),"")
}