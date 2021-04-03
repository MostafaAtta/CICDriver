package com.atta.cicdriver.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atta.cicdriver.R

class Home1Fragment : Fragment() {

    private lateinit var home1ViewModel: Home1ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        home1ViewModel =
            ViewModelProvider(this).get(Home1ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home1, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        home1ViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}