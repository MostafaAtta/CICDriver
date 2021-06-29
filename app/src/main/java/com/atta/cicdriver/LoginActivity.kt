package com.atta.cicdriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.atta.cicdriver.databinding.ActivityLoginBinding
import com.atta.cicdriver.model.Route
import com.atta.cicdriver.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var binding: ActivityLoginBinding

    private val TAG = "LoginActivity"
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener(this)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth

        db = Firebase.firestore
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signInButton -> signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                checkUser()
            }
            .addOnFailureListener {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", it)
                // ...
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                updateUI(null)
            }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            //Toast.makeText(this, "Not yet", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkUser(){
        val email = Firebase.auth.currentUser?.email
        db.collection("Drivers")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty){

                    Toast.makeText(this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show()

                    /*val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()*/
                }else{
                    for (document in it){

                        val user = document.toObject(User::class.java)
                        user.id = document.id
                        if (user.enabled){
                            SessionManager.with(this).login(user)

                            if (user.type == "1"){
                                getRouteData(document.id)
                            }else{
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            Toast.makeText(this, getString(com.atta.cicdriver.R.string.check_your_access), Toast.LENGTH_LONG).show()

                        }

                    }

                }
            }
            .addOnFailureListener {
                Log.d(TAG, "get failed with ", it)
                Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
            }

    }
    private fun getRouteData(id: String) {
        if (SessionManager.with(this).getUserId() != "") {
            db.collection("Routes")
                    .whereEqualTo("driverId", id)
                    .get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            for (document in it) {
                                val route = document.toObject(Route::class.java)
                                SessionManager.with(this).saveRouteName(route.name)
                                SessionManager.with(this).saveRouteId(document.id)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
        }
    }

}