package com.example.mandiexe.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.mandiexe.ui.home.PriceFragment
import com.example.mandiexe.ui.home.StockFragment


class PagerAdapter(fm: FragmentManager?, var mNumOfTabs: Int) :
    FragmentStatePagerAdapter(fm!!) {

    private val tabTitles = arrayOf("Mandi Prices", "Stocks")

    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PriceFragment()
            1 -> StockFragment()
            else -> PriceFragment()
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}