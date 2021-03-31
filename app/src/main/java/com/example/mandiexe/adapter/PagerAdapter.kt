package com.example.mandiexe.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.mandiexe.R
import com.example.mandiexe.ui.demands.MyDemandsFragment
import com.example.mandiexe.ui.supply.MySuppliesFragment
import com.example.mandiexe.utils.ApplicationUtils


class MyViewPagerAdapter(manager: FragmentManager) :
    FragmentStatePagerAdapter(manager) {


    private val mCrops = ApplicationUtils.getContext().resources.getString(R.string.str_my_crops)
    private val mReq = ApplicationUtils.getContext().resources.getString(R.string.str_my_req)

    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        return when (position) {

            0 -> MySuppliesFragment.newInstance()
            1 -> MyDemandsFragment.newInstance()
            else -> MySuppliesFragment.newInstance()
        }
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {

        return when (position) {
            0 -> mCrops
            1 -> mReq
            else -> ""
        }

    }

    companion object {
        private const val NUM_ITEMS = 2
    }

}
