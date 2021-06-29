package com.atta.cicdriver.ui

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.atta.cicdriver.LoginActivity
import com.atta.cicdriver.R
import com.atta.cicdriver.SessionManager
import com.atta.cicdriver.SplashScreenActivity
import com.atta.cicdriver.databinding.FragmentMoreBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MoreFragment : Fragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var db: FirebaseFirestore

    var adminPhone = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        val view = binding.root

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth

        db = Firebase.firestore

        getAdminPhone(false)

        binding.logout.setOnClickListener(this)
        if (SessionManager.with(requireContext()).getUserType() == "0"){
            binding.sosImg.visibility = View.GONE
        }else if (SessionManager.with(requireContext()).getUserType() == "1"){
            binding.driverUserCard.visibility = View.GONE
            binding.studentUserCard.visibility = View.GONE
            binding.routeRequestCard.visibility = View.GONE
        }

        binding.sosImg.setOnClickListener {
            showSosDialog()
        }

        binding.languageCard.setOnClickListener {
            showLanguageDialog()
        }

        binding.driverUserCard.setOnClickListener {
            Navigation
                    .findNavController(it)
                    .navigate(MoreFragmentDirections.actionNavigationMoreToDriversAccountsFragment())
        }

        binding.studentUserCard.setOnClickListener {
            Navigation
                    .findNavController(it)
                    .navigate(MoreFragmentDirections.actionNavigationMoreToStudentsAccountsFragment())
        }

        binding.routeRequestCard.setOnClickListener {
            Navigation
                    .findNavController(it)
                    .navigate(MoreFragmentDirections.actionNavigationMoreToRouteRequestsFragment())
        }

        binding.profileCard.setOnClickListener {
            Navigation
                    .findNavController(it)
                    .navigate(MoreFragmentDirections.actionNavigationMoreToProfileFragment())
        }
        return view
    }

    private fun getAdminPhone(call: Boolean) {
        db.collection("Drivers")
            .whereEqualTo("type", "0")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (doc in it){
                        if (call){
                            adminPhone = doc["phone"].toString()
                            callAdmin()
                        }else {
                            adminPhone = doc["phone"].toString()
                        }
                    }
                }
            }
    }

    private fun showLanguageDialog() {

        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.language_layout)
        val arBtn = dialog?.findViewById(R.id.ar_btn) as Button
        val enBtn = dialog.findViewById(R.id.en_btn) as Button
        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        arBtn.setOnClickListener {
            changeLanguage("ar")
            dialog.dismiss()
        }
        enBtn.setOnClickListener {
            changeLanguage("en")
            dialog.dismiss() }
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show()
    }

    fun changeLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        context?.let { SessionManager.with(it).setLanguage(language) }
        val intent = Intent(context, SplashScreenActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    private fun showSosDialog() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.sos_layout)
        val policeBtn = dialog?.findViewById(R.id.police_btn) as Button
        val fireStationBtn = dialog.findViewById(R.id.fireStation_btn) as Button
        val ambulanceBtn = dialog.findViewById(R.id.ambulance_btn) as Button
        val adminBtn = dialog.findViewById(R.id.admin_btn) as Button
        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        policeBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "122")
            startActivity(dialIntent)
            dialog.dismiss()
        }
        fireStationBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "180")
            startActivity(dialIntent)
            dialog.dismiss() }
        ambulanceBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "123")
            startActivity(dialIntent)
            dialog.dismiss()
        }
        adminBtn.setOnClickListener {

            if (adminPhone != "") {
                callAdmin()
            }else{
                getAdminPhone(true)
            }

            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show()

    }

    private fun callAdmin() {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$adminPhone")
        startActivity(dialIntent)
    }

    private fun logOut() {
        // Firebase sign out
        auth.signOut()

        context?.let { SessionManager.with(it).logout() }

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(context, LoginActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.logout -> logOut()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}