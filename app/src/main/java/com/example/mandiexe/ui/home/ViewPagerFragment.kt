package com.example.mandiexe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mandiexe.R

/**
 * A simple [Fragment] subclass.
 */
class ViewPagerFragment : Fragment() {


    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.viewpager_fragment, container, false)




        return root

    }

}


