package com.example.mandiexe.lib

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.utils.auth.PreferenceUtil


object OfflineTranslate {

    fun translateToEnglish(context: Context, query: String): String {

        //Get instance for the viewmodel
        val viewModel =
            ViewModelProviders.of(context as FragmentActivity).get(TranslateViewmodel::class.java)
        val pref = PreferenceUtil.getLanguageFromPreference()

        if (pref != null && pref =="en") {
            return query
        }
        viewModel.sourceLang.value = TranslateViewmodel.Language(pref ?: "en")
        viewModel.sourceText.value = query
        viewModel.targetLang.value = TranslateViewmodel.Language("en")

        var mText = query
        viewModel.translatedText.observe(context, Observer { resultOrError ->
            resultOrError?.let {
                if (it.error != null) {
                    Log.e(
                        "Offline",
                        "Error for english trans" + resultOrError.error?.localizedMessage
                    )
                } else {
                    mText = resultOrError.result ?: query
                }
            }
        })
        Log.e("Returning from Offline ", "Ans for text " + query + " -" + mText)
        return mText
    }

    fun translateToDefault(context: Context, query: String): String {


        //Get instance for the viewmodel
        val viewModel =
            ViewModelProviders.of(context as FragmentActivity).get(TranslateViewmodel::class.java)

        val pref = PreferenceUtil.getLanguageFromPreference()
        if (pref != null && pref == "en") {
            return query
        }

        //Dont run model
        viewModel.sourceLang.value = TranslateViewmodel.Language(pref ?: "en")
        viewModel.sourceText.value = query
        viewModel.targetLang.value = TranslateViewmodel.Language("en")

        var mText = query
        viewModel.translatedText.observe(context, Observer { resultOrError ->
            resultOrError?.let {
                if (it.error != null) {
                    Log.e(
                        "Offline",
                        "Error for english trans" + resultOrError.error?.localizedMessage
                    )
                } else {
                    mText = resultOrError.result ?: query
                }
            }
        })
        Log.e("Returning from Offline ", "Ans for text " + query + " -" + mText)
        return mText
    }


    //Extetnal Utils
    // Set up toggle buttons to delete or download remote models locally.
//    buttonSyncSource.setOnCheckedChangeListener { _, isChecked ->
//        val language = adapter.getItem(sourceLangSelector.selectedItemPosition)
//        language?.let {
//            if (isChecked) {
//                viewModel.downloadLanguage(language)
//            } else {
//                viewModel.deleteLanguage(language)
//            }
//        }
//    }
//    buttonSyncTarget.setOnCheckedChangeListener { _, isChecked ->
//        val language = adapter.getItem(targetLangSelector.selectedItemPosition)
//        language?.let {
//            if (isChecked) {
//                viewModel.downloadLanguage(language)
//            } else {
//                viewModel.deleteLanguage(language)
//            }
//        }
//    }
//
//    // Translate input text as it is typed
//    sourceText.addTextChangedListener(object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//
//        override fun afterTextChanged(s: Editable) {
//            setProgressText(targetText)
//            viewModel.sourceText.postValue(s.toString())
//        }
//    })


}