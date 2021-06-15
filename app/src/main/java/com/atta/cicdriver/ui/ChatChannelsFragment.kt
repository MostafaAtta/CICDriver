package com.atta.cicdriver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicdriver.adapter.PersonsAdapter
import com.atta.cicdriver.SessionManager
import com.atta.cicdriver.model.UserRoute
import com.atta.cicdriver.databinding.FragmentChatChannelsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatChannelsFragment : Fragment() {

    private var _binding: FragmentChatChannelsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    var users: ArrayList<UserRoute> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatChannelsBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        getUsers()
        return view
    }


    private fun getUsers() {
        if (users.isNotEmpty()){
            users.clear()
        }
        db.collection("user_route")
                .whereEqualTo("driverId", context?.let { SessionManager.with(it).getUserId() })
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){
                            val user = document.toObject(UserRoute::class.java)
                            user.id = document.id
                            users.add(user)

                            //addRoute(route)
                        }
                        //checklists = it.toObjects<Checklist>()
                        showRecycler()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

    }


    private fun showRecycler() {
        val routesAdapter = activity?.let { PersonsAdapter(users, it) }

        binding.recyclerViewPersons.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewPersons.adapter = routesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}