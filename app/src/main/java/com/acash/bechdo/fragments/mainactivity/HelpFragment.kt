package com.acash.bechdo.fragments.mainactivity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.adapters.FaqAdapter
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment : Fragment() {

    private val arrQues = arrayOf("Lorem ipsum dolor sit amet, consectetur adipiscin elit.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit."
    )

    private val arrAns = arrayOf("Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis.",
        "Lorem ipsum dolor sit amet, consectetur adipiscin elit. Morbi sagittis turpis quam, non venenatis risus sollicitudin non. Sed nec turpis."
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

            if(requireContext().packageManager.resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY)!=null){
                startActivity(intent)
            }else{
                Toast.makeText(requireContext(),"No suitable apps found for sending Email",Toast.LENGTH_SHORT).show()
            }
        }
    }
}