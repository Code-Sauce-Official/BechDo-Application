package com.acash.bechdo.models

import com.acash.bechdo.R

data class ProductPictures(val ProductPics: Int) {
    companion object {
        fun getPostPics(): ArrayList<ProductPictures> {

            val list: ArrayList<ProductPictures> = arrayListOf<ProductPictures>()

            list.add(ProductPictures(R.drawable.pic1))
            list.add(ProductPictures(R.drawable.pic2))
            list.add(ProductPictures(R.drawable.pic3))
            list.add(ProductPictures(R.drawable.pic4))
            list.add(ProductPictures(R.drawable.mobile_phone))


            return list
        }
    }
}


