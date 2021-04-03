package com.atta.cicdriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.atta.cicdriver.databinding.ActivityRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val TAG = "RegisterActivity"

    private lateinit var db: FirebaseFirestore

    lateinit var phone:  String
    lateinit var lastName: String
    lateinit var firstName: String
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email").toString()
        binding.emailTv.setText(email)

        db = Firebase.firestore

        binding.done.setOnClickListener {
            phone = binding.phoneTv.text.toString()
            lastName = binding.lastNameTv.text.toString()
            firstName = binding.firstNameTv.text.toString()
            if (verify()){
                val user = mapOf("email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "phone" to phone)
                createAccount(user)
            }
        }
    }


    private fun verify(): Boolean{
        when {
            firstName.isEmpty() -> {
                binding.firstNameTv.error = "Please enter your first name"
                binding.firstNameTv.requestFocus()
                return false
            }
            lastName.isEmpty() -> {
                binding.lastNameTv.error = "Please enter your first name"
                binding.lastNameTv.requestFocus()
                return false
            }
            phone.isEmpty() -> {
                binding.phoneTv.error = "Please enter your first name"
                binding.phoneTv.requestFocus()
                return false
            }
            else -> {

                return true
            }
        }
    }

    private fun createAccount(user: Map<String, String>){

        db.collection("Drivers").add(user)
            .addOnSuccessListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.d(TAG, "get failed with ", it)
                Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
            }
    }
}