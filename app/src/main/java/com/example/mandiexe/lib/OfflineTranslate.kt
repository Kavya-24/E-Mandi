package com.example.mandiexe.lib

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions


object OfflineTranslate {

    fun translateToEnglish(context: Context, query: String): String {

        //Get instance for the viewmodel
        val viewModel =
            ViewModelProviders.of(context as FragmentActivity).get(TranslateViewmodel::class.java)
        val pref = PreferenceUtil.getLanguageFromPreference()

        if (pref != null && pref == "en") {
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

    fun translateToDefault(context: Context, query: String, tvInstance: TextView) {

        Log.e("Offline", "In translate to default")

        val pref = PreferenceUtil.getLanguageFromPreference()
        if (pref != null && pref == "en") {
            //Set the textView q
            tvInstance.setText(query)
            return
        }


        //Make translator
        // Create an English-Deafult translator:
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(pref ?: "en")
            .build()
        val mTranslator = Translation.getClient(options)
        (context as LifecycleOwner).lifecycle.addObserver(mTranslator)

        val conditions = DownloadConditions.Builder()
            .build()

        mTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Log.e("Offline", "In suuccess modle")
                successModelFound(mTranslator, query, tvInstance)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                //Set the Enlgilsg query
                tvInstance.setText(query)
                Log.e("Offline", "Model not downloaded" + exception.cause + exception.message)
            }

        Log.e(
            "Returning from Offline ",
            "Ans for text " + query + " -" + tvInstance.text.toString()
        )

        //Close translator
        //mTranslator.close()

        return
    }

    private fun successModelFound(
        mTranslator: Translator,
        query: String,
        tvInstance: TextView
    ) {


        mTranslator.translate(query)
            .addOnSuccessListener { translatedText ->
                // Translation successful.
                tvInstance.setText(translatedText.toString())
                Log.e("Offline", "Translated text q = $query" + translatedText.toString())
            }
            .addOnFailureListener { exception ->
                // Error.
                // ...
                tvInstance.setText(query)
                Log.e("Offline Exception", exception.message + exception.cause)
            }


        return
    }


}