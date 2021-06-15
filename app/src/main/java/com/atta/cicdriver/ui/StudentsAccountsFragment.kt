package com.atta.cicdriver.ui

import android.app.Dialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicdriver.R
import com.atta.cicdriver.adapter.UsersAdapter
import com.atta.cicdriver.databinding.FragmentStudentsAccountsBinding
import com.atta.cicdriver.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentsAccountsFragment : Fragment() {

    private var _binding: FragmentStudentsAccountsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    private var students: ArrayList<User> = ArrayList()

    private var studentsAdapter: UsersAdapter? = null

    private var newStudents = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStudentsAccountsBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        getNewStudents()

        binding.swipeLayout.setOnRefreshListener {
            updateList()
            binding.swipeLayout.isRefreshing = false
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            newStudents = isChecked
            updateList()
        }
        showRecycler()
        return view
    }

    private fun updateList() {
        if (newStudents) {
            binding.switch1.text = getString(R.string.new_requests)
            getNewStudents()
        } else {
            binding.switch1.text = getString(R.string.old_requests)
            getOldStudents()
        }
    }

    private fun getOldStudents() {
        students.clear()
        studentsAdapter?.notifyDataSetChanged()
        db.collection("Users")
                .whereEqualTo("enabled", true)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){
                            val user = document.toObject(User::class.java)
                            user.id = document.id
                            students.add(user)

                            //addRoute(route)
                        }
                        //checklists = it.toObjects<Checklist>()
                        studentsAdapter?.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

    }

    private fun getNewStudents() {
        students.clear()
        studentsAdapter?.notifyDataSetChanged()
        db.collection("Users")
                .whereEqualTo("enabled", false)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){
                            val user = document.toObject(User::class.java)
                            user.id = document.id
                            students.add(user)

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

    private fun showRecycler() {
        studentsAdapter = UsersAdapter(students, this)

        binding.studentsRecycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.studentsRecycler.adapter = studentsAdapter
    }

    fun showStudentPopup(user: User){
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.student_acc_layout)
        val enableBtn = dialog?.findViewById(R.id.enableBtn) as Button
        if (user.enabled){
            enableBtn.setBackgroundResource(R.drawable.rect_round_red_color)
            enableBtn.text = getString(R.string.disable)
        }
        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        enableBtn.setOnClickListener {

            enableDisableUser(user.id, !user.enabled)
            dialog.dismiss()
        }

        (dialog.findViewById(R.id.nameTxt) as TextView).text = "${user.firstName} ${user.lastName}"
        (dialog.findViewById(R.id.emailTxt) as TextView).text = user.email
        (dialog.findViewById(R.id.collageIdTxt) as TextView).text = user.collegeId
        (dialog.findViewById(R.id.phoneTxt) as TextView).text = user.phone

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show()
    }

    private fun enableDisableUser(id: String, enable: Boolean) {
        db.collection("Users")
            .document(id)
            .update(mapOf("enabled" to enable))
            .addOnSuccessListener {

                Toast.makeText(context, "User updated", Toast.LENGTH_LONG).show()
                updateList()
            }
            .addOnFailureListener {

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val TAG = "StudentsAccountsFragment"

        var instance: StudentsAccountsFragment? = null

        fun getStudentsAccountsInstance(): StudentsAccountsFragment?{
            return instance
        }
    }

}