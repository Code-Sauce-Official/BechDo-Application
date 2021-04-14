package com.acash.bechdo.models

import com.acash.bechdo.R

class Categories (val id: Int, val caption: String) {

    companion object{
        fun getCategories(): ArrayList<Categories> = arrayListOf(
            Categories(R.drawable.music, "Instruments"),
                Categories(R.drawable.stationery, "Stationery"),
                Categories(R.drawable.electronics, "Electronics"),
                Categories(R.drawable.sports, "Sports"),
                Categories(R.drawable.books, "Books"),
                Categories(R.drawable.rent, "Rent")
        )
    }
}