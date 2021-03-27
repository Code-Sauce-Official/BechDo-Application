package com.acash.bechdo

class Categories (val id: Int, val caption: String) {

    companion object{
        fun getCategories(): ArrayList<Categories> {

            val list: ArrayList<Categories> = arrayListOf<Categories>()

            list.add(Categories(R.drawable.music, "Instruments"))
            list.add(Categories(R.drawable.stationery, "Stationery"))
            list.add(Categories(R.drawable.electronics, "Electronics"))
            list.add(Categories(R.drawable.sports, "Sports"))
            list.add(Categories(R.drawable.books, "Books"))
            list.add(Categories(R.drawable.rent, "Rent"))

            return list
        }
    }
}