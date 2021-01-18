package com.example.mandiexe.lib

import android.icu.text.Transliterator
import android.os.Build
import androidx.annotation.RequiresApi


class TransliterationFile {

    /**
     * @param args the command line arguments
     */

    @RequiresApi(Build.VERSION_CODES.Q)
    fun latinToDevanagari(latinString: String?): String? {
        val ENG_TO_DEV = "Latin-Devanagari"
        val toDevnagiri = Transliterator.getInstance(ENG_TO_DEV)
        return toDevnagiri.transliterate(latinString)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun latinToJapanese(latinString: String?): String? {
        val ENG_TO_JAP = "Latin-Katakana"
        val toJapanese = Transliterator.getInstance(ENG_TO_JAP)
        return toJapanese.transliterate(latinString)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun result(args: Array<String>) {
        // TODO code application logic here
        println(latinToDevanagari("Anish"))
        println(latinToJapanese("Anish"))
    }
}