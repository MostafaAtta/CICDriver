package com.atta.cicdriver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.atta.cicdriver.databinding.FragmentMoreBinding
import com.atta.cicdriver.model.Route
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore

    private lateinit var googleSignInClient: GoogleSignInClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = Firebase.auth
        db = Firebase.firestore
        if (SessionManager.with(this).getUserType() == "1") {
            getRouteData()
        }else{
            startHandler()
        }

    }

    private fun logout() {


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Firebase sign out
        auth.signOut()

        SessionManager.with(this).logout()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkUser() {
        val userID = SessionManager.with(this).getUserId()
        db.collection("Drivers")
            .document(SessionManager.with(this).getUserId())
            .get()
            .addOnSuccessListener {

                val enabled = it["enabled"] as Boolean

                if (enabled){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }
            .addOnFailureListener {

                startHandler()
                Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
            }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getRouteData(){
        if (SessionManager.with(this).getUserId() != "") {
            db.collection("Routes")
                    .whereEqualTo("driverId", SessionManager.with(this).getUserId())
                    .get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            for (document in it) {
                                val route = document.toObject(Route::class.java)
                                SessionManager.with(this).saveRouteName(route.name)
                                SessionManager.with(this).saveRouteId(document.id)

                                startHandler()
                            }
                        }else{
                            startHandler()
                        }
                    }
                    .addOnFailureListener {

                        startHandler()
                        Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
                    }
        }else{

            startHandler()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startHandler() {
        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler(Looper.getMainLooper()).postDelayed({
            changeLanguage(SessionManager.with(this).getLanguage())
            val result = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

            if (result != PackageManager.PERMISSION_GRANTED) {
                requestPermission()
            }else{
                val currentUser = auth.currentUser
                if(currentUser == null){
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    checkUser()
                }
            }


        }, Companion.SPLASH_TIME_OUT)// 3000 is the delayed time in milliseconds.
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permission, FINE_LOCATION_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val currentUser = auth.currentUser
        if(currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            checkUser()
        }
    }

    private fun changeLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }

    companion object {
        // This is the loading time of the splash screen
        private const val SPLASH_TIME_OUT:Long = 1000 // 1 sec


        private const val FINE_LOCATION_REQUEST_CODE = 101
    }

}