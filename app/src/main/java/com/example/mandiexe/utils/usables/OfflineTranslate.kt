package com.example.mandiexe.utils.usables

import android.content.Context
import android.icu.text.Transliterator
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import com.example.mandiexe.R
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*


object OfflineTranslate {


    private val TAG = OfflineTranslate::class.java.simpleName

    fun translateToEnglish(context: Context, query: String, tvInstance: TextView) {


        val pref = PreferenceUtil.getLanguageFromPreference()

        //No need to convert
        if (pref != null && pref == "en") {
            //Set the textView q
            tvInstance.text = query.capitalize(Locale.ROOT)
            return
        }
        //In case of say an emoty descrytion
        if (query == "" || query.isEmpty() || query.isBlank()) {
            tvInstance.text = context.resources.getString(R.string.noDesc)
            return
        }

        // Create an Default-English translator:
        try {

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(pref ?: "en")
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()

            val mTranslator = Translation.getClient(options)
            (context as LifecycleOwner).lifecycle.addObserver(mTranslator)

            val conditions = DownloadConditions.Builder()
                .build()

            mTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {

                    successModelFound(mTranslator, query, tvInstance)
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    //Set the Enlgilsg query
                    tvInstance.setText(query)
                    Log.e("TAG", "Model not downloaded" + exception.cause + exception.message)
                }

            Log.e(
                "Returning from TAG ",
                "Ans for text " + query + " -" + tvInstance.text.toString()
            )

            //Close translator
            //mTranslator.close()
        } catch (e: Exception) {
            //Might be a traslator closed exception
            Log.e(TAG, "Translator closed maybe but exception is " + e.cause + e.message)
        }

        return
    }

    fun translateToDefault(context: Context, query: String, tvInstance: TextView) {


        val pref = PreferenceUtil.getLanguageFromPreference()
        if (pref != null && pref == "en") {
            //Set the textView q
            tvInstance.setText(query.capitalize(Locale("en")))
            return
        }


        //Make translator
        // Create an English-Deafult translator:
        try {
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
                    successModelFound(mTranslator, query, tvInstance)
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    //Set the Enlgilsg query
                    tvInstance.setText(query)
                    Log.e("TAG", "Model not downloaded" + exception.cause + exception.message)
                }

            Log.e(
                "Returning from TAG ",
                "Ans for text " + query + " -" + tvInstance.text.toString()
            )

            //Close translator
            //mTranslator.close()
        } catch (e: Exception) {
            //Might be a traslator closed exception
            Log.e(TAG, "Translator closed maybe but exception is " + e.cause + e.message)
        }

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

            }
            .addOnFailureListener { exception ->
                // Error.
                // ...
                tvInstance.setText(query)
                Log.e("TAG Exception", exception.message + exception.cause)
            }


        return
    }

    fun translateFunction(query: String, tvInstance: TextView) {


//        if (pref.getLanguageFromPreference() == "en") {
//            tvInstance.setText(query)
//            return
//        }
//
//        //Else, use external operations
//
//
//        viewModel.sourceLang.value = TranslateViewmodel.Language("en")
//        viewModel.sourceText.value = query
//        viewModel.targetLang.value =
//            TranslateViewmodel.Language(pref.getLanguageFromPreference() ?: "en")
//
//
//        //Observe
//        viewModel.translatedText.observe(
//            itemView.context as LifecycleOwner,
//            Observer { resultOrError ->
//                resultOrError?.let {
//                    if (it.error != null) {
//                        Log.e(
//                            "TAG",
//                            "Error for english trans" + resultOrError.error?.localizedMessage
//                        )
//                    } else {
//
//                        tvInstance.text = resultOrError.result ?: query
//                        Log.e(
//                            "TAG",
//                            "Sucess result for q $query is ${resultOrError.result}"
//                        )
//                    }
//                }
//            })
//
//
//        //Remove observers
//
//        viewModel.sourceText.removeObserver { mObserver ->
//            val x = mObserver
//            Log.e("TAG", "In remove sText observer")
//        }
//
//        viewModel.sourceLang.removeObserver { mObserver ->
//            val x = mObserver
//            Log.e("TAG", "In remove sLanf observer")
//        }
//        viewModel.targetLang.removeObserver { mObserver ->
//            val x = mObserver
//            Log.e("TAG", "In remove tLang observer")
//        }
//        viewModel.translatedText.removeObserver { mObserver ->
//            val x = mObserver
//            Log.e("TAG", "In remove tLang observer")
//        }
//        viewModel.availableModels.removeObserver { mObserver ->
//            val x = mObserver
//        }
//

    }

    //Transliterate
    @RequiresApi(Build.VERSION_CODES.Q)
    fun transliterateToDefault(latinString: String?): String {


        //Dont translate if the default lamguage is en
        if (PreferenceUtil.getLanguageFromPreference() == "en") {
            return latinString!!
        }

        Log.e(
            TAG,
            "Tranliterator  " + Transliterator.getDisplayName(
                "en-" + PreferenceUtil.getLanguageFromPreference().toString(),
                Locale(PreferenceUtil.getLanguageFromPreference().toString())
            )
        )
        val toDevnagiri = Transliterator.getInstance(
            "en-" + PreferenceUtil.getLanguageFromPreference().toString()
        )
        return toDevnagiri.transliterate(latinString)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun transliterateToEnglish(latinString: String?): String? {

        //Dont translate if the default lamguage is en
        if (PreferenceUtil.getLanguageFromPreference() == "en") {
            return latinString
        }
        Log.e(TAG, "Tranliterator  " + Transliterator.getDisplayName("en-hi", Locale("hi")))

        val toEnglish = Transliterator.getInstance(
            PreferenceUtil.getLanguageFromPreference().toString() + "-en"
        )
        return toEnglish.transliterate(latinString)

    }

}