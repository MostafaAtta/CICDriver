package com.atta.cicdriver.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.PersonItemBinding
import com.atta.cicdriver.model.UserRoute
import com.atta.cicdriver.ui.ChatChannelsFragmentDirections

open class PersonsAdapter (private val data: List<UserRoute>,
                           private val activity: Activity):
    RecyclerView.Adapter<PersonsAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: PersonItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PersonItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = data[position]

        with(holder){
            with(user){

                binding.textViewName.text = userName
            }



            binding.root.setOnClickListener {
                Navigation.findNavController(activity, R.id.nav_host_fragment)
                        .navigate(ChatChannelsFragmentDirections.actionNavigationChannelsToChatFragment(user.userId, user.userName))
            }

        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}