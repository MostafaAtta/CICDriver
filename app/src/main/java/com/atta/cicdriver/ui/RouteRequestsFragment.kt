package com.atta.cicdriver.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicdriver.R
import com.atta.cicdriver.adapter.RouteRequestAdapter
import com.atta.cicdriver.databinding.FragmentRouteRequestsBinding
import com.atta.cicdriver.model.RouteRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RouteRequestsFragment : Fragment() {

    private var _binding: FragmentRouteRequestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    private var requests: ArrayList<RouteRequest> = ArrayList()

    private var requestAdapter: RouteRequestAdapter? = null

    private var newRequests = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRouteRequestsBinding.inflate(inflater, container, false)
        val view = binding.root
        db = Firebase.firestore

        getRequests()

        binding.swipeLayout.setOnRefreshListener {
            requests.clear()
            if (newRequests){
                binding.switch1.text = getString(R.string.new_requests)
                getRequests()
            }else{
                binding.switch1.text = getString(R.string.old_requests)
                getOldRequests()
            }
            binding.swipeLayout.isRefreshing = false
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            newRequests = isChecked
            if (isChecked){
                buttonView.text = getString(R.string.new_requests)
                getRequests()
            }else{
                buttonView.text = getString(R.string.old_requests)
                getOldRequests()
            }
        }

        return view
    }

    private fun getRequests() {
        requests.clear()
        requestAdapter?.notifyDataSetChanged()
        db.collection("Route requests")
            .whereEqualTo("status", "New")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (document in it){
                        val request = document.toObject(RouteRequest::class.java)
                        request.id = document.id
                        requests.add(request)

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

    private fun getOldRequests() {
        requests.clear()
        requestAdapter?.notifyDataSetChanged()
        db.collection("Route requests")
                .whereNotEqualTo("status", "New")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){
                            val request = document.toObject(RouteRequest::class.java)
                            request.id = document.id
                            requests.add(request)

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
        requestAdapter = RouteRequestAdapter(requests)

        binding.requestsRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.requestsRecycler.adapter = requestAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}