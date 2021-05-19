package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_filters.*

class FiltersFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val postsFragment = parentFragment as PostsFragment
        val chips = tagsGroup.children
            .toList()

        for(filter in postsFragment.filterTags) {
            chips.filter { (it as Chip).text == filter }
                .forEach {
                    (it as Chip).isChecked = true
                }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }

        applyBtn.setOnClickListener {
            val filters = ArrayList<String>()

            chips.filter { (it as Chip).isChecked }
                .forEach {
                    filters.add((it as Chip).text.toString())
                }

                val fragmentToSet = PostsFragment()
                val bundle = Bundle()
                bundle.putString("Query", postsFragment.newQuery)

                if(filters.isEmpty()){
                    bundle.putString("Task", "Products")
                }else {
                    bundle.putString("Task", "Filters")
                    bundle.putStringArrayList("FilterTags", filters)
                }

                fragmentToSet.arguments = bundle
                dismiss()
                (activity as MainActivity).setFragment(fragmentToSet)
        }
    }
}