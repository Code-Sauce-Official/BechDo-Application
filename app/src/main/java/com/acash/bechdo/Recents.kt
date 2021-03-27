package com.acash.bechdo

class Recents (val id: Int, val pname: String, val price: String) {

    companion object{
        fun getRecents(): ArrayList<Recents>{

            val list = arrayListOf<Recents>()

            list.add(0, Recents(R.drawable.cycle, "Cycle", "80$"))
            list.add(0, Recents(R.drawable.guitar, "Guitar", "100$"))
            list.add(0, Recents(R.drawable.cycle, "Cycle", "90$"))
            list.add(0, Recents(R.drawable.guitar, "Guitar", "120$"))

            return list
        }
    }
}