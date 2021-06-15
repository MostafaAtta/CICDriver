package com.atta.cicdriver.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.RoutesItemBinding
import com.atta.cicdriver.model.Route
import com.atta.cicdriver.ui.RoutesFragmentDirections

open class RoutesAdapter (private val data: List<Route>,
                          private val activity: Activity):
    RecyclerView.Adapter<RoutesAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: RoutesItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RoutesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val route = data[position]

        with(holder){
            with(route){
                binding.routeName.text = name
                binding.startTime.text = morningTimes["startTime"]
                binding.endTime.text = afternoonTimes["startTime"]

            }


            binding.root.setOnClickListener {
                Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(RoutesFragmentDirections.actionRoutesFragmentToRouteDetailsFragment(route, route.name))
            }
        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}