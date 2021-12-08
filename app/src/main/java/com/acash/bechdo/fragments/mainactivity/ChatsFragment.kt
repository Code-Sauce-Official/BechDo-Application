package com.acash.bechdo.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.BuildConfig
import com.acash.bechdo.viewholders.InboxViewHolder
import com.acash.bechdo.R
import com.acash.bechdo.activities.*
import com.acash.bechdo.models.Inbox
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment() {
    private lateinit var inboxAdapter: FirebaseRecyclerAdapter<Inbox, InboxViewHolder>

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseDatabase.getInstance(BuildConfig.RTDB_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupAdapter()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    private fun setupAdapter() {
        val baseQuery = database.reference.child("inbox/${auth.uid}")
            .orderByChild("time/time")

        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery,Inbox::class.java)
            .build()

        inboxAdapter = object : FirebaseRecyclerAdapter<Inbox, InboxViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder =
                InboxViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false))

            override fun onBindViewHolder(holder: InboxViewHolder, position: Int, inbox: Inbox) {
                holder.bind(inbox) { name, uid, thumbImg ->
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra(NAME, name)
                    intent.putExtra(UID, uid)
                    intent.putExtra(THUMBIMG, thumbImg)
                    startActivity(intent)
                }
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)

                if(this@ChatsFragment::inboxAdapter.isInitialized)
                    inboxAdapter.stopListening()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true

        chatRview.apply {
            layoutManager = mLayoutManager
            adapter = inboxAdapter
        }
    }
}