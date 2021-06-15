package com.atta.cicdriver.adapter

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicdriver.R
import com.atta.cicdriver.SessionManager
import com.atta.cicdriver.databinding.MessageItemBinding
import com.atta.cicdriver.model.Message
import java.text.SimpleDateFormat

open class MessagesAdapter (private val data: List<Message>,
                            private val activity: Activity):
    RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: MessageItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MessageItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = data[position]

        with(holder){
            with(message){
                if (senderId == SessionManager.with(activity).getUserId()) {
                    binding.root.apply {
                        binding.backgroundLy.setBackgroundResource(R.drawable.rect_round_white)
                        val lParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END)
                        binding.backgroundLy.layoutParams = lParams
                    }
                }
                else {
                    binding.root.apply {
                        binding.backgroundLy.setBackgroundResource(R.drawable.rect_round_primary_color)
                        val lParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.START)
                        binding.backgroundLy.layoutParams = lParams
                    }
                }
                binding.textViewMessageText.text = text
                val dateFormat = SimpleDateFormat
                        .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
                binding.textViewMessageTime.text = dateFormat.format(time.toDate())

            }

        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}