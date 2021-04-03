package com.atta.cicdriver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = Firebase.auth

        db = Firebase.firestore
        getRouteData()


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
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }


        }, Companion.SPLASH_TIME_OUT)// 3000 is the delayed time in milliseconds.
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permission, FINE_LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val currentUser = auth.currentUser
        if(currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        // This is the loading time of the splash screen
        private const val SPLASH_TIME_OUT:Long = 1000 // 1 sec


        private const val FINE_LOCATION_REQUEST_CODE = 101
    }

}