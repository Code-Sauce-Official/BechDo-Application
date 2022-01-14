package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.models.Colleges
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_filters.*
import java.util.*
import kotlin.collections.HashSet

class FiltersFragment : BottomSheetDialogFragment() {

    private lateinit var clgSet: HashSet<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clgDropDown.setDropDownBackgroundResource(R.color.white)

        val postsFragment = parentFragment as PostsFragment

        if (postsFragment.task == "Category") {
            tvCategory.visibility = View.GONE
            tagsGroup.visibility = View.GONE
        }

        val clgList = ArrayList<String>()

        val clgAdapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item_dropdown_menu,
            R.id.tvClgName,
            clgList
        )

        clgDropDown.setAdapter(clgAdapter)

        FirebaseFirestore.getInstance().collection("Colleges").document("Names")
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val colleges = it.toObject(Colleges::class.java)
                    colleges?.apply {
                        clgList.clear()
                        clgList.addAll(listOfColleges)
                        clgList.sort()
                        clgSet = HashSet(clgList)
                        clgAdapter.notifyDataSetChanged()

                        if (postsFragment.clg != "")
                            clgDropDown?.setText(postsFragment.clg)

                        if (clgInput?.isErrorEnabled == true && !clgDropDown?.text.isNullOrEmpty()) {
                            clgInput?.isErrorEnabled = false
                        }
                    }
                }
            }

        clgDropDown.addTextChangedListener {
            clgInput.isErrorEnabled = false
        }

        if (postsFragment.category != "" && postsFragment.task != "Category") {
            tagsGroup.children
                .toList()
                .filter { (it as Chip).text == postsFragment.category }
                .forEach {
                    (it as Chip).isChecked = true
                }
        }

        if (postsFragment.priceRange != "") {
            priceRangeGroup.children
                .toList()
                .filter { (it as Chip).text == postsFragment.priceRange }
                .forEach {
                    (it as Chip).isChecked = true
                }
        }

        if (postsFragment.type != -1)
            typeRadio.check(typeRadio.getChildAt(postsFragment.type).id)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        applyBtn.setOnClickListener {
            val clg = clgDropDown.text.toString()

            if (checkClgErrors(clg)) {
                val fragmentToSet = PostsFragment()
                var isFilterSelected = false
                val bundle = Bundle()
                bundle.putString("Query", postsFragment.newQuery)

                if (postsFragment.task != "Category") {
                    tagsGroup.findViewById<Chip>(tagsGroup.checkedChipId)
                        ?.let {
                            bundle.putString("CategoryFilter", it.text.toString())
                            isFilterSelected = true
                        }
                } else {
                    bundle.putString("CategoryFilter", postsFragment.category)
                }

                if (typeRadio.checkedRadioButtonId != -1) {
                    val type =
                        typeRadio.indexOfChild(typeRadio.findViewById<RadioButton>(typeRadio.checkedRadioButtonId))
                    bundle.putInt("Type", type)
                    isFilterSelected = true
                }

                priceRangeGroup.findViewById<Chip>(priceRangeGroup.checkedChipId)
                    ?.let {
                        val priceRange = it.text.toString()

                        bundle.putString("PriceRange", priceRange)
                        isFilterSelected = true
                    }

                if (clg != "") {
                    bundle.putString("College", clg)
                    isFilterSelected = true
                }

                when {
                    postsFragment.task == "Category" -> {
                        bundle.putString("Task", "Category")
                    }
                    isFilterSelected -> {
                        bundle.putString("Task", "Filters")
                    }
                    else -> bundle.putString("Task", "Products")
                }

                fragmentToSet.arguments = bundle
                dismiss()
                (activity as MainActivity).setFragment(fragmentToSet)
            }
        }
    }

    private fun checkClgErrors(clg: String): Boolean {
        if (clg == "") {
            return true
        }

        if (!(::clgSet.isInitialized)) {
            clgInput.error =
                "Waiting for College list to load, hence college filter is unavailable."
            return false
        }

        if (!clgSet.contains(clg)) {
            clgInput.error = "Please select a college from the given list.."
            return false
        }

        return true
    }
}