package com.example.mandiexe.ui.authUi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.viewmodels.LanguageViewModel

class LanguageFragment : Fragment(), OnMyLanguageListener {

    companion object {
        fun newInstance() = LanguageFragment()
    }

    private lateinit var viewModel: LanguageViewModel
    private lateinit var root: View


    //To be replaced by rv of languge
    //Default will be english

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.language_fragment, container, false)

        createLanguageList()



        return root
    }

    private fun createLanguageList() {

        val mLanguages: MutableList<LanguageBody> = mutableListOf()

        mLanguages.add(LanguageBody("English"))
        mLanguages.add(LanguageBody("Hindi"))
        mLanguages.add(LanguageBody("Bengali"))


        val rv = root.findViewById<RecyclerView>(R.id.rv_language_main)
        val mAdapter = LanguagesAdapter(this)
        mAdapter.lst = mLanguages
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = mAdapter


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LanguageViewModel::class.java)

    }

    override fun selectLanguage(_listItem: LanguageBody) {

    }

}
