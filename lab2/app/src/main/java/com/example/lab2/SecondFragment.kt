package com.example.lab2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SecondFragment : Fragment() {
    private lateinit var drawingView: DrawingView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("SecondFragment", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        Log.d("SecondFragment", "View inflated")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SecondFragment", "onViewCreated called")

        drawingView = view.findViewById(R.id.drawing_view)
        Log.d("SecondFragment", "DrawingView found: ${drawingView != null}")

        if (DrawingStateManager.hasState()) {
            drawingView.setPoints(DrawingStateManager.loadState())
            Log.d("SecondFragment", "State loaded")
        }

        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            Log.d("SecondFragment", "Back button clicked")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SecondFragment", "onDestroyView called")
        try {
            DrawingStateManager.saveState(drawingView.getPoints())
            Log.d("SecondFragment", "State saved")
        } catch (e: Exception) {
            Log.e("SecondFragment", "Error saving state", e)
        }
    }
}