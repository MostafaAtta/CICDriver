package com.atta.cicdriver.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.atta.cicdriver.R
import com.atta.cicdriver.SessionManager
import com.atta.cicdriver.databinding.FragmentEditDriverAccBinding
import com.atta.cicdriver.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        binding.emailTv.setText(context?.let { SessionManager.with(it).getEmail() })
        binding.firstNameTv.setText(context?.let { SessionManager.with(it).getFirstName() })
        binding.lastNameTv.setText(context?.let { SessionManager.with(it).getLastName() })
        binding.phoneTv.setText(context?.let { SessionManager.with(it).getPhone() })

        if (context?.let { SessionManager.with(it).getUserType() } == "0"){
            binding.routeGroup.visibility = View.GONE
        }

        binding.done.setOnClickListener {
            updateAcc()
        }
        
        return view

    }

    private fun updateAcc() {
        val acc = mapOf("email" to binding.emailTv.text.toString(),
            "firstName" to binding.firstNameTv.text.toString(),
            "lastName" to binding.lastNameTv.text.toString(),
            "phone" to binding.phoneTv.text.toString())
        context?.let { SessionManager.with(it).getUserId() }?.let {
            db.collection("Drivers")
                .document(it)
                .update(acc)
                .addOnSuccessListener {

                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}