package com.engear.vansales.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.engear.vansales.R
import com.engear.vansales.databinding.FragmentOnBoard3Binding


/**
 * A simple [Fragment] subclass.
 * Use the [OnBoard3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoard3Fragment : Fragment() {

    var binding: FragmentOnBoard3Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_on_board3, container, false
        )
        val view = binding!!.root

        return view
    }


}