package com.example.mandiexe.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.mandiexe.R
import com.example.mandiexe.ui.home.MyCropBidsFragment
import com.example.mandiexe.ui.home.RequirementFragment


class PagerAdapter(fm: FragmentManager?, var mNumOfTabs: Int, context: Context) :
    FragmentStatePagerAdapter(fm!!) {

    private val mCrops = context.resources.getString(R.string.str_my_crops)
    private val mReq = context.resources.getString(R.string.str_my_req)

    private val tabTitles = arrayOf(mCrops, mReq)

    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MyCropBidsFragment()
            1 -> RequirementFragment()
            else -> MyCropBidsFragment()
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}