package com.atta.cicdriver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.databinding.SamplesItemBinding
import com.atta.cicdriver.ui.ChatFragment
import com.atta.cicdriver.model.Message
import com.atta.cicdriver.model.MessageSample
import com.google.firebase.Timestamp
import java.util.*

open class SamplesAdapter(
    private val data: List<MessageSample>,
    private val fragment: ChatFragment,
    private val userId: String,
    private val otherUserId: String,
    private val userName: String,
    private val language: String
):
    RecyclerView.Adapter<SamplesAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: SamplesItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SamplesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sample = data[position]

        with(holder){
            with(sample){

                if (language == "en") {
                    binding.textViewMessage.text = message
                    binding.root.setOnClickListener {
                        fragment.sendMessage(Message(message,
                            Timestamp(Calendar.getInstance().time), userId, otherUserId,
                            userName, "to user"))
                    }
                }else{

                    binding.textViewMessage.text = messageAr
                    binding.root.setOnClickListener {
                        fragment.sendMessage(Message(messageAr,
                            Timestamp(Calendar.getInstance().time), userId, otherUserId,
                            userName, "to user"))
                    }
                }

            }

        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}