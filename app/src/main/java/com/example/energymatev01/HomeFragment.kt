package com.example.energymatev01

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var userName: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("user_data", 0)

        userName = view.findViewById(R.id.usernameText)

        val storedUserName = sharedPreferences.getString("name", "")
        userName.text = storedUserName?.uppercase()

    }
}