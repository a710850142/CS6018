package com.example.lab2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class FirstFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("FirstFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FirstFragment", "onViewCreated called")

        val button = view.findViewById<Button>(R.id.navigate_button)

        if (DrawingStateManager.hasState()) {
            button.text = "Keep Drawing"
        } else {
            button.text = "Start Drawing"
        }

        button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SecondFragment())
                .addToBackStack(null)
                .commit()
            Log.d("FirstFragment", "Navigation button clicked")
        }
    }
}