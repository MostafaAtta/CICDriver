package com.atta.cicdriver

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.atta.cicdriver.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    lateinit var appBarConfiguration: AppBarConfiguration

    private val TAG = "MainActivity"

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_channels,
                        R.id.navigation_more, R.id.navigation_routes
                )
        )
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        when (SessionManager.with(this).getUserType()) {
            "0" -> {
                navView.menu.getItem(2).isVisible = false

            }
            "1" -> {
                navView.menu.getItem(1).isVisible = false

            }

        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        db = Firebase.firestore

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful){
                Log.w(TAG, "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }

            val token = it.result

            Log.d(TAG, token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

            getTokens(token)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())    }



    fun setTokens(tokens: MutableList<String>){
        db.collection("Drivers")
            .document(SessionManager.with(this).getUserId())
            .update(mapOf("tokens" to tokens))
    }

    fun getTokens(token: String) {
        db.collection("Drivers")
            .document(SessionManager.with(this).getUserId())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!

                if (!user.tokens.contains(token)){
                    user.tokens.add(token)
                    setTokens(user.tokens)
                }
            }
    }
}