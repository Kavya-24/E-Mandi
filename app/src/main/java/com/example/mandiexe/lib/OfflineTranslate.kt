package com.example.mandiexe.lib

import android.util.Log
import android.widget.Toast
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions


object OfflineTranslate {

    //Lang is the preference utils
    fun translateOfflineToEnglish(text: String): String {

        val lang = PreferenceUtil.getLanguageFromPreference() ?: "en"
        val ctx = ApplicationUtils.getContext()

        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(
                FirebaseTranslateLanguage.languageForLanguageCode(lang)
                    ?: FirebaseTranslateLanguage.EN
            )
            .setTargetLanguage(FirebaseTranslateLanguage.EN)
            .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        var q = ""
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { mText ->
                        Log.e("Offline", "In english" + mText)
                        q = mText
                    }
                    .addOnFailureListener { mException ->
                        Log.e("Offline", "In english" + mException.message + mException.cause)

                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    ctx,
                    "Failed to download the translation model; please try again ...",
                    Toast.LENGTH_SHORT
                ).show()
                it.printStackTrace()
            }

        if (q == "") {
            q = text
        }

        return q;
    }

    fun translateOfflineToDefault(text: String): String {

        val lang = PreferenceUtil.getLanguageFromPreference() ?: "en"
        val ctx = ApplicationUtils.getContext()

        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(
                FirebaseTranslateLanguage.languageForLanguageCode(lang)
                    ?: FirebaseTranslateLanguage.EN
            )
            .build()

        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        var q = ""
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { mText ->
                        Log.e("Offline", "In english to default" + mText)
                        q = mText
                    }
                    .addOnFailureListener { mException ->
                        Log.e(
                            "Offline",
                            "In english to default" + mException.message + mException.cause
                        )

                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    ctx,
                    "Failed to download the translation model; please try again ...",
                    Toast.LENGTH_SHORT
                ).show()
                it.printStackTrace()
            }

        if (q == "") {
            q = text
        }

        return q;
    }


}