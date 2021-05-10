package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.adapters.PostPicturesAdapter
import com.acash.bechdo.models.ProductPictures
import kotlinx.android.synthetic.main.fragment_post_information.*


class PostInformation : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_information, container, false)


        val postpics = ProductPictures.getPostPics()
        val picsadpater = PostPicturesAdapter(postpics)

        rvpostpics.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvpostpics.adapter = picsadpater


    }


}



