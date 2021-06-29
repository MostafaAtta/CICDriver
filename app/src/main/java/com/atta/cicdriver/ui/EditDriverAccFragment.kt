package com.atta.cicdriver.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.atta.cicdriver.databinding.FragmentEditDriverAccBinding
import com.atta.cicdriver.model.Route
import com.atta.cicdriver.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Compiler.enable

class EditDriverAccFragment : Fragment() {

    private var _binding: FragmentEditDriverAccBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    var routes: ArrayList<Route> = ArrayList()

    var routesNames: ArrayList<String> = ArrayList()

    private lateinit var driver: User

    lateinit var adapter: ArrayAdapter<String>

    lateinit var newRoute: Route

    var newRouteName: String = ""
    var newRouteId : String = ""
    var oldRouteId : String = ""
    var oldDriverId : String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditDriverAccBinding.inflate(inflater, container, false)
        val view = binding.root

        driver = arguments?.let { EditDriverAccFragmentArgs.fromBundle(it).driver }!!

        db = Firebase.firestore

        binding.emailTv.setText(driver.email)
        binding.firstNameTv.setText(driver.firstName)
        binding.lastNameTv.setText(driver.lastName)
        binding.phoneTv.setText(driver.phone)

        binding.switch2.isChecked = driver.enabled
        updateSwitchText(driver.enabled)

        binding.switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            updateSwitchText(isChecked)
            showEnableDialog(isChecked)
        }

        binding.done.setOnClickListener {
            updateAcc()
        }

        getRoutes()

        return view
    }

    private fun updateSwitchText(enabled: Boolean) {
        if (enabled) {
            binding.switch2.text = getString(com.atta.cicdriver.R.string.enabled)
            binding.switch2.setTextColor(resources.getColor(com.atta.cicdriver.R.color.green))
        } else {
            binding.switch2.text = getString(com.atta.cicdriver.R.string.disabled)
            binding.switch2.setTextColor(resources.getColor(com.atta.cicdriver.R.color.red))
        }
    }

    private fun showEnableDialog(checked: Boolean) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(if(checked) getString(com.atta.cicdriver.R.string.enbale_account) else getString(com.atta.cicdriver.R.string.disable_account))
        builder.setMessage(if(checked) getString(com.atta.cicdriver.R.string.enbale_q) else getString(com.atta.cicdriver.R.string.disable_q))

        builder.setPositiveButton(com.atta.cicdriver.R.string.yes) { dialog, which ->
            enableDisable(checked)
        }

        builder.setNegativeButton(com.atta.cicdriver.R.string.no) { dialog, which ->
            binding.switch2.isChecked = !checked
            updateSwitchText(!checked)

        }

        builder.show()
    }

    private fun enableDisable(enable: Boolean) {
        val acc = mapOf("enabled" to enable)
        db.collection("Drivers")
            .document(driver.id)
            .update(acc)
            .addOnSuccessListener {
                Toast.makeText(context, if(enable) getString(com.atta.cicdriver.R.string.account_enabled)
                else getString(com.atta.cicdriver.R.string.account_disabled), Toast.LENGTH_LONG).show()


            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun updateAcc() {
        val acc = mapOf("email" to binding.emailTv.text.toString(),
            "firstName" to binding.firstNameTv.text.toString(),
            "lastName" to binding.lastNameTv.text.toString(),
            "phone" to binding.phoneTv.text.toString(),
            "routeName" to newRouteName,
            "routeId" to newRouteId)
        val routeChanged = newRouteId != driver.routeId
        db.collection("Drivers")
            .document(driver.id)
            .update(acc)
            .addOnSuccessListener {
                if (routeChanged){
                    updateRoute()
                }else{
                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                    activity?.let { it1 -> Navigation
                        .findNavController(it1, com.atta.cicdriver.R.id.nav_host_fragment)
                        .navigate(EditDriverAccFragmentDirections.actionEditDriverAccFragmentToNavigationMore())}
                }

            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun updateRoute() {
        val route = mapOf("driverName" to "${driver.firstName} ${driver.lastName}",
            "driverId" to driver.id)
        db.collection("Routes")
            .document(newRouteId)
            .update(route)
            .addOnSuccessListener {
                if (driver.routeId != ""){
                    updateOldRoute()
                }else if (newRoute.driverId != ""){
                    updateOldDriver()
                }else{
                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                    activity?.let { it1 -> Navigation
                        .findNavController(it1, com.atta.cicdriver.R.id.nav_host_fragment)
                        .navigate(EditDriverAccFragmentDirections.actionEditDriverAccFragmentToNavigationMore())}
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun updateOldRoute() {
        val route = mapOf("driverName" to "",
            "driverId" to "")
        oldRouteId = driver.routeId
        db.collection("Routes")
            .document(oldRouteId)
            .update(route)
            .addOnSuccessListener {
                if (newRoute.driverId != ""){
                    updateOldDriver()
                }else{
                    Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                    activity?.let { it1 -> Navigation
                        .findNavController(it1, com.atta.cicdriver.R.id.nav_host_fragment)
                        .navigate(EditDriverAccFragmentDirections.actionEditDriverAccFragmentToNavigationMore())}
                }

            }
            .addOnFailureListener {
                Toast.makeText(context, getString(com.atta.cicdriver.R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun updateOldDriver() {
        val driver = mapOf("routeName" to "",
            "routeId" to "")
        db.collection("Drivers")
            .document(newRoute.driverId)
            .update(driver)
            .addOnSuccessListener {

                Toast.makeText(context, getString(com.atta.cicdriver.R.string.done), Toast.LENGTH_LONG).show()
                activity?.let { it1 -> Navigation
                    .findNavController(it1, com.atta.cicdriver.R.id.nav_host_fragment)
                    .navigate(EditDriverAccFragmentDirections.actionEditDriverAccFragmentToNavigationMore())}


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
                    if (driver.routeId != "") {
                        binding.routeSpinner.setSelection(routesNames.indexOf(driver.routeName))
                    }
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
                R.layout.simple_list_item_1,
                routesNames
            )
        }!!

        binding.routeSpinner.adapter = adapter
        if (driver.routeId != "") {
            binding.routeSpinner.setSelection(routesNames.indexOf(driver.routeName))
        }

        binding.routeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                newRouteName = routesNames[i]
                newRouteId = routes[i].id
                oldDriverId = routes[i].driverId
                newRoute = routes[i]
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