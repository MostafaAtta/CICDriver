package com.atta.cicdriver.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicdriver.R
import com.atta.cicdriver.adapter.DriversAdapter
import com.atta.cicdriver.databinding.FragmentDriversAccountsBinding
import com.atta.cicdriver.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DriversAccountsFragment : Fragment() {

    private var _binding: FragmentDriversAccountsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    private var drivers: ArrayList<User> = ArrayList()

    private var driversAdapter: DriversAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDriversAccountsBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        getDrivers()

        binding.swipeLayout.setOnRefreshListener {
            getDrivers()
            binding.swipeLayout.isRefreshing = false
        }

        binding.addDriver.setOnClickListener{
            Navigation.findNavController(it)
                .navigate(DriversAccountsFragmentDirections.actionDriversAccountsFragmentToNewDriverFragment())
        }

        return view
    }

    private fun getDrivers(){
        drivers.clear()
        driversAdapter?.notifyDataSetChanged()
        db.collection("Drivers")
            .whereEqualTo("type", "1")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (document in it){
                        val user = document.toObject(User::class.java)
                        user.id = document.id
                        drivers.add(user)

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

    fun callDriver(phone: String){
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phone")
        activity?.startActivity(dialIntent)
    }

    private fun showRecycler() {
        driversAdapter = DriversAdapter(drivers, this)

        binding.driversRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.driversRecycler.adapter = driversAdapter
    }

    fun deleteDriverAcc(user: User) {

    }

    fun showDriverDetails(user: User){
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.driver_acc_layout)
        val editBtn = dialog?.findViewById(R.id.editBtn) as Button

        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        editBtn.setOnClickListener {

            activity?.let { it1 ->
                Navigation.findNavController(it1, R.id.nav_host_fragment)
                    .navigate(DriversAccountsFragmentDirections
                        .actionDriversAccountsFragmentToEditDriverAccFragment(user))
            }

            dialog.dismiss()
        }

        (dialog.findViewById(R.id.nameTxt) as TextView).text = "${user.firstName} ${user.lastName}"
        (dialog.findViewById(R.id.emailTxt) as TextView).text = user.email
        (dialog.findViewById(R.id.routeNameTxt) as TextView).text = user.routeName
        (dialog.findViewById(R.id.phoneTxt) as TextView).text = user.phone

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        private const val TAG = "DriversAccountsFragment"

        var instance: DriversAccountsFragment? = null

        fun getDriversAccountsInstance(): DriversAccountsFragment?{
            return instance
        }
    }

}