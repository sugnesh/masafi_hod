package com.engear.vansales.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.engear.vansales.R
import com.engear.vansales.databinding.FragmentOnBoard1Binding

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoard1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoard1Fragment : Fragment() {

    var binding: FragmentOnBoard1Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_on_board1, container, false
        )
        val view = binding!!.root

        return view
    }

}