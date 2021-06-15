package com.atta.cicdriver.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.RouteRequestItemBinding
import com.atta.cicdriver.model.RouteRequest
import com.atta.cicdriver.ui.ChatChannelsFragmentDirections
import com.atta.cicdriver.ui.RouteRequestsFragmentDirections
import com.atta.cicdriver.ui.RoutesFragmentDirections

open class RouteRequestAdapter (private val data: List<RouteRequest>):
    RecyclerView.Adapter<RouteRequestAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: RouteRequestItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RouteRequestItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val routeRequest = data[position]

        with(holder){
            with(routeRequest){

                binding.routeNameTxt.text = routeName
                binding.userNameTxt.text = userName
            }



            binding.root.setOnClickListener {
                Navigation.findNavController(it)
                        .navigate(RouteRequestsFragmentDirections.actionRouteRequestsFragmentToRequestDetailsFragment(routeRequest))
            }

        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}