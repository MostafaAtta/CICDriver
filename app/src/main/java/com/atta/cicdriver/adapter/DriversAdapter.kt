package com.atta.cicdriver.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.DriverItemBinding
import com.atta.cicdriver.databinding.RouteRequestItemBinding
import com.atta.cicdriver.databinding.StudentItemBinding
import com.atta.cicdriver.model.RouteRequest
import com.atta.cicdriver.model.User
import com.atta.cicdriver.ui.*

open class DriversAdapter (private val data: List<User>,private val fragment: DriversAccountsFragment):
    RecyclerView.Adapter<DriversAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: DriverItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DriverItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val routeRequest = data[position]

        with(holder){
            with(routeRequest){

                binding.textViewName.text = "$firstName $lastName"
                binding.textViewEmail.text = email
                binding.textViewRouteName.text = routeName

                binding.deleteImg.setOnClickListener {
                    fragment.deleteDriverAcc(this)
                }
                binding.callImg.setOnClickListener {
                    fragment.callDriver(this.phone)
                }
                binding.root.setOnClickListener {
                    fragment.showDriverDetails(this)
                }
            }




        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}