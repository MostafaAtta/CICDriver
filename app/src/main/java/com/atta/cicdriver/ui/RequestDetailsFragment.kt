package com.atta.cicdriver.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.FragmentRequestDetailsBinding
import com.atta.cicdriver.model.Route
import com.atta.cicdriver.model.RouteRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RequestDetailsFragment : Fragment() {

    private var _binding: FragmentRequestDetailsBinding? = null
    private val binding get() = _binding!!

    lateinit var db: FirebaseFirestore

    lateinit var request: RouteRequest

    var previousRouteId: String? = null

    lateinit var route: Route

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentRequestDetailsBinding.inflate(inflater, container, false)

        request = arguments?.let { RequestDetailsFragmentArgs.fromBundle(it).request }!!

        db = Firebase.firestore

        initiateViews()

        return binding.root
    }

    private fun initiateViews() {
        binding.studentNameTxt.text = request.userName
        binding.routeNameTxt.text = request.routeName
        binding.status.text = request.status
        getOldRoute()
        getRouteDetails()
        binding.approveBtn.setOnClickListener {
            approveRequest()
        }
        binding.rejectBtn.setOnClickListener {
            rejectRequest()
        }

        if (request.status == "New"){
            binding.approveBtn.visibility = View.VISIBLE
            binding.rejectBtn.visibility = View.VISIBLE
        }else{
            binding.approveBtn.visibility = View.GONE
            binding.rejectBtn.visibility = View.GONE
        }
    }

    private fun getRouteDetails(){
        db.collection("Routes")
                .document(request.routeId)
                .get()
                .addOnSuccessListener {
                    route = it.toObject(Route::class.java)!!
                    route.id = it.id
                    if (route != null) {
                        binding.availableTxt.text = (route.busCapacity -
                                route.reservedChairs).toString()
                        binding.capacityTxt.text = (route.busCapacity).toString()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Can't get route details", Toast.LENGTH_LONG).show()
                }
    }

    private fun getOldRoute(){

        db.collection("user_route")
                .whereEqualTo("userId", request.userId)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty){
                        binding.previousTitle.visibility = View.GONE
                        binding.previousRouteTxt.visibility = View.GONE
                    }else{
                        for (doc in it){
                            binding.previousRouteTxt.text = doc["routeName"].toString()
                            previousRouteId = doc.id
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Can't get user route details", Toast.LENGTH_LONG).show()
                }

    }

    private fun approveRequest(){
        db.collection("Route requests")
                .document(request.id)
                .update(mapOf("status" to "Approved"))
                .addOnSuccessListener {
                    addRouteToUser()
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()
                }
    }

    private fun addRouteToUser() {
        val userRoute = mapOf("routeId" to route.id ,
                "routeName" to route.name,
                "driverId" to route.driverId,
                "driverName" to route.driverName)
        if (previousRouteId == null){
            db.collection("user_route")
                    .document()
                    .set(userRoute)
                    .addOnSuccessListener {
                        updateStatus(getString(R.string.approved), getString(R.string.request_approved))
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()
                    }
        }else{
            db.collection("user_route")
                    .document(previousRouteId.toString())
                    .update(userRoute)
                    .addOnSuccessListener {
                        updateStatus(getString(R.string.approved), getString(R.string.done))
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()
                    }
        }
    }

    private fun updateStatus(status: String, msg: String) {
        binding.approveBtn.visibility = View.GONE
        binding.rejectBtn.visibility = View.GONE
        binding.status.text = status
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun rejectRequest(){
        db.collection("Route requests")
                .document(request.id)
                .update(mapOf("status" to "Rejected"))
                .addOnSuccessListener {
                    updateStatus(getString(R.string.rejected), getString(R.string.request_rejected))

                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()
                }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}