package com.example.mandiexe.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var root: View
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private val TAG = HomeFragment::class.java.simpleName

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "In on resume")
//        updateViews()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_home, container, false)

        tabLayout = root.findViewById<View>(R.id.tabs) as TabLayout

        viewPager = root.findViewById<View>(R.id.viewpager) as ViewPager

        updateViews()

        return root
    }

    private fun updateViews() {
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        viewPager.adapter =
            context?.let {
                com.example.mandiexe.adapter.PagerAdapter(
                    fragmentManager, tabLayout.tabCount,
                    it
                )
            }

        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }


}
