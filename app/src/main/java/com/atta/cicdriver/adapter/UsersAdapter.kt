package com.atta.cicdriver.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.RouteRequestItemBinding
import com.atta.cicdriver.databinding.StudentItemBinding
import com.atta.cicdriver.model.RouteRequest
import com.atta.cicdriver.model.User
import com.atta.cicdriver.ui.ChatChannelsFragmentDirections
import com.atta.cicdriver.ui.RouteRequestsFragmentDirections
import com.atta.cicdriver.ui.RoutesFragmentDirections
import com.atta.cicdriver.ui.StudentsAccountsFragment

open class UsersAdapter (private val data: List<User>, private val fragment: StudentsAccountsFragment):
    RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: StudentItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StudentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val routeRequest = data[position]

        with(holder){
            with(routeRequest){

                binding.textViewName.text = "$firstName $lastName"
                binding.textViewEmail.text = email
                binding.collegeId.text = collegeId

                binding.root.setOnClickListener {
                    fragment.showStudentPopup(this)
                }
            }




        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}