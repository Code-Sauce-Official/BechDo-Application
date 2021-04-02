package com.acash.bechdo.models

import com.acash.bechdo.R

class Recents (val id: Int, val pname: String, val price: String) {

    companion object{
        fun getRecents() = arrayListOf(
            Recents(R.drawable.cycle, "Cycle", "80$"),
            Recents(R.drawable.phone, "Smartphone", "100$"),
            Recents(R.drawable.cycle, "Cycle", "90$"),
            Recents(R.drawable.phone, "Smartphone", "120$")
        )
    }
}