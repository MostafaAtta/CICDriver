package com.atta.cicdriver.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.Navigation
import com.atta.cicdriver.databinding.FragmentNewDriverBinding
import com.atta.cicdriver.model.Route
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class NewDriverFragment : Fragment() {

    private var _binding: FragmentNewDriverBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    var routes: ArrayList<Route> = ArrayList()

    var routesNames: ArrayList<String> = ArrayList()

    lateinit var adapter: ArrayAdapter<String>

    lateinit var route: Route

    var oldDriverId : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewDriverBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        binding.done.setOnClickListener {
            checkAcc()
        }

        getRoutes()

        return view
    }

    private fun checkAcc() {
        db.collection("Drivers")
            .whereEqualTo("email", binding.emailTv.text.toString())
            .get()
            .addOnSuccessListener {
                if (it.isEmpty){
                    createAcc()
                }else{
                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.user_exist), Toast.LENGTH_LONG).show()

                }

            }
            .addOnFailureListener{
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()
            }

    }


    private fun createAcc() {
        val acc = mapOf("email" to binding.emailTv.text.toString().toLowerCase(Locale.ROOT),
            "firstName" to binding.firstNameTv.text.toString(),
            "lastName" to binding.lastNameTv.text.toString(),
            "phone" to binding.phoneTv.text.toString(),
            "routeName" to route.name,
            "routeId" to route.id,
            "type" to "1")
        db.collection("Drivers")
            .add(acc)
            .addOnSuccessListener {

                updateRoute(it.id)
            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun updateRoute(id: String) {
        val routeMap =
            mapOf("driverName" to "${binding.firstNameTv.text.toString()} ${binding.lastNameTv.text.toString()}",
            "driverId" to id)
        db.collection("Routes")
            .document(route.id)
            .update(routeMap)
            .addOnSuccessListener {
                if (route.driverId != ""){
                    updateOldDriver()
                }else{
                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                    activity?.let { it1 -> Navigation
                        .findNavController(it1, com.atta.cicdriver.R.id.nav_host_fragment)
                        .navigate(NewDriverFragmentDirections.actionNewDriverFragmentToNavigationMore())}
                }

            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun updateOldDriver() {
        val driver = mapOf("routeName" to "",
            "routeId" to "")
        oldDriverId = route.driverId
        db.collection("Drivers")
            .document(oldDriverId)
            .update(driver)
            .addOnSuccessListener {

                Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                activity?.let { it1 -> Navigation
                    .findNavController(it1, com.atta.cicdriver.R.id.nav_host_fragment)
                    .navigate(NewDriverFragmentDirections.actionNewDriverFragmentToNavigationMore())}


            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun getRoutes(){
        db.collection("Routes")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (document in it){
                        val route = document.toObject(Route::class.java)
                        route.id = document.id
                        routes.add(route)
                        routesNames.add(route.name)

                        //addRoute(route)
                    }
                    //checklists = it.toObjects<Checklist>()
                    setRoutesSpinner()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun setRoutesSpinner(){
        adapter = context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_list_item_1,
                routesNames
            )
        }!!

        binding.routeSpinner.adapter = adapter

        binding.routeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                route = routes[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {

    }

}