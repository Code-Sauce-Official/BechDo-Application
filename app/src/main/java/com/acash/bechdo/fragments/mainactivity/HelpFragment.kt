package com.acash.bechdo.fragments.mainactivity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.adapters.FaqAdapter
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment : Fragment() {

    private val arrQues = arrayOf(
        R.string.faqs_ques_1,
        R.string.faqs_ques_2,
        R.string.faqs_ques_3,
        R.string.faqs_ques_4,
        R.string.faqs_ques_5,
        R.string.faqs_ques_6,
        R.string.faqs_ques_7
    )

    private val arrAns = arrayOf(
        R.string.faqs_ans_1,
        R.string.faqs_ans_2,
        R.string.faqs_ans_3,
        R.string.faqs_ans_4,
        R.string.faqs_ans_5,
        R.string.faqs_ans_6,
        R.string.faqs_ans_7
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val faqAdapter = FaqAdapter(arrQues,arrAns)
        rvFaq.apply {
            adapter = faqAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL,arrayOf("Bechdoofficial@gmail.com"))

            try{
                startActivity(intent)
            }catch (e:ActivityNotFoundException){
                Toast.makeText(requireContext(),"No suitable apps found for sending Email",Toast.LENGTH_SHORT).show()
            }
        }
    }
}