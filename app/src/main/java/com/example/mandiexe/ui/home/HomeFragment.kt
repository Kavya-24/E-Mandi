package com.example.mandiexe.ui.home
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.viewpager.widget.ViewPager
//import com.example.mandiexe.R
//import com.example.mandiexe.adapter.MyViewPagerAdapter
//import com.example.mandiexe.viewmodels.HomeViewModel
//import com.google.android.material.tabs.TabLayout
//
//
//class HomeFragment : Fragment() {
//
//    private val homeViewModel: HomeViewModel by viewModels()
//    private lateinit var root: View
//    private lateinit var tabLayout: TabLayout
//    private lateinit var viewPager: ViewPager
//
//    private val TAG = HomeFragment::class.java.simpleName
//    private lateinit var pagerAdapter: MyViewPagerAdapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.e(TAG, "In on view created")
//        viewPager.adapter = pagerAdapter
//
//        //Set the tabbed layout
//        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
//
//        //Set the tabbed layout
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                Log.e(TAG, "In on tab selected with position ${tab?.position}")
//                viewPager.currentItem = tab!!.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                Log.e(TAG, "In on tab UN_selected with position ${tab?.position}")
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                Log.e(TAG, "In on tab RE_selected with position ${tab?.position}")
//
//            }
//        })
//
//
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        Log.e(TAG, "In on resume")
//
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        Log.e(TAG, "In onCreateView")
//        root = inflater.inflate(R.layout.fragment_home, container, false)
//
//        tabLayout = root.findViewById<View>(R.id.tabs) as TabLayout
//
//
//        viewPager = root.findViewById<View>(R.id.viewpagerHome) as ViewPager
//        pagerAdapter = MyViewPagerAdapter(childFragmentManager)
//
//
//
//        return root
//    }
//
//
//    override fun onDestroy() {
//        Log.e(
//            TAG,
//            "In on destroy with current viewpager position = ${viewPager.currentItem} and tab layout current position = ${tabLayout.selectedTabPosition}"
//        )
//
//        if (viewPager.currentItem == 0) {
//            //Default brhaviour
//            super.onDestroy()
//        } else {
//            viewPager.currentItem = viewPager.currentItem - 1
//            super.onDestroy()
//
//        }
//
//
//    }
//
//
//}
