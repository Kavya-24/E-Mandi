package com.example.mandiexe.lib

import android.util.Log
import java.util.*


class ConversionTable internal constructor() {

    private val TAG = "Conversation Table"
    private var unicode: Hashtable<String, String>? = null

    private fun populateHashTable() {
        unicode = Hashtable()
        // unicode
        unicode!!["\u0901"] = "rha" // anunAsika - cchandra bindu, using ~ to // *
        unicode!!["\u0902"] = "n" // anusvara
        unicode!!["\u0903"] = "ah" // visarga
        unicode!!["\u0940"] = "ee"
        unicode!!["\u0941"] = "u"
        unicode!!["\u0942"] = "oo"
        unicode!!["\u0943"] = "rhi"
        unicode!!["\u0944"] = "rhee" //  * = Doubtful Case
        unicode!!["\u0945"] = "e"
        unicode!!["\u0946"] = "e"
        unicode!!["\u0947"] = "e"
        unicode!!["\u0948"] = "ai"
        unicode!!["\u0949"] = "o"
        unicode!!["\u094a"] = "o"
        unicode!!["\u094b"] = "o"
        unicode!!["\u094c"] = "au"
        unicode!!["\u094d"] = ""
        unicode!!["\u0950"] = "om"
        unicode!!["\u0958"] = "k"
        unicode!!["\u0959"] = "kh"
        unicode!!["\u095a"] = "gh"
        unicode!!["\u095b"] = "z"
        unicode!!["\u095c"] = "dh" // *
        unicode!!["\u095d"] = "rh"
        unicode!!["\u095e"] = "f"
        unicode!!["\u095f"] = "y"
        unicode!!["\u0960"] = "ri"
        unicode!!["\u0961"] = "lri"
        unicode!!["\u0962"] = "lr" //  *
        unicode!!["\u0963"] = "lree" //  *
        unicode!!["\u093E"] = "aa"
        unicode!!["\u093F"] = "i"

        //  Vowels and Consonants...
        unicode!!["\u0905"] = "a"
        unicode!!["\u0906"] = "a"
        unicode!!["\u0907"] = "i"
        unicode!!["\u0908"] = "ee"
        unicode!!["\u0909"] = "u"
        unicode!!["\u090a"] = "oo"
        unicode!!["\u090b"] = "ri"
        unicode!!["\u090c"] = "lri" // *
        unicode!!["\u090d"] = "e" // *
        unicode!!["\u090e"] = "e" // *
        unicode!!["\u090f"] = "e"
        unicode!!["\u0910"] = "ai"
        unicode!!["\u0911"] = "o"
        unicode!!["\u0912"] = "o"
        unicode!!["\u0913"] = "o"
        unicode!!["\u0914"] = "au"
        unicode!!["\u0915"] = "k"
        unicode!!["\u0916"] = "kh"
        unicode!!["\u0917"] = "g"
        unicode!!["\u0918"] = "gh"
        unicode!!["\u0919"] = "ng"
        unicode!!["\u091a"] = "ch"
        unicode!!["\u091b"] = "chh"
        unicode!!["\u091c"] = "j"
        unicode!!["\u091d"] = "jh"
        unicode!!["\u091e"] = "ny"
        unicode!!["\u091f"] = "t" // Ta as in Tom
        unicode!!["\u0920"] = "th"
        unicode!!["\u0921"] = "d" // Da as in David
        unicode!!["\u0922"] = "dh"
        unicode!!["\u0923"] = "n"
        unicode!!["\u0924"] = "t" // ta as in tamasha
        unicode!!["\u0925"] = "th" // tha as in thanks
        unicode!!["\u0926"] = "d" // da as in darvaaza
        unicode!!["\u0927"] = "dh" // dha as in dhanusha
        unicode!!["\u0928"] = "n"
        unicode!!["\u0929"] = "nn"
        unicode!!["\u092a"] = "p"
        unicode!!["\u092b"] = "ph"
        unicode!!["\u092c"] = "b"
        unicode!!["\u092d"] = "bh"
        unicode!!["\u092e"] = "m"
        unicode!!["\u092f"] = "y"
        unicode!!["\u0930"] = "r"
        unicode!!["\u0931"] = "rr"
        unicode!!["\u0932"] = "l"
        unicode!!["\u0933"] = "ll" // the Marathi and Vedic 'L'
        unicode!!["\u0934"] = "lll" // the Marathi and Vedic 'L'
        unicode!!["\u0935"] = "v"
        unicode!!["\u0936"] = "sh"
        unicode!!["\u0937"] = "ss"
        unicode!!["\u0938"] = "s"
        unicode!!["\u0939"] = "h"

        // represent it\
        //  unicode.put("\u093c","'"); // avagraha using "'"
        //  unicode.put("\u093d","'"); // avagraha using "'"
        unicode!!["\u0969"] = "3" // 3 equals to pluta
        unicode!!["\u014F"] = "Z" // Z equals to upadhamaniya
        unicode!!["\u0CF1"] =
            "V" // V equals to jihvamuliya....but what character have u settled for jihvamuliya
        /*   unicode.put("\u0950","Ω"); // aum
        unicode.put("\u0958","κ"); // Urdu qaif
        unicode.put("\u0959","Κ"); //Urdu qhe
        unicode.put("\u095A","γ"); // Urdu gain
        unicode.put("\u095B","ζ"); //Urdu zal, ze, zoe
        unicode.put("\u095E","φ"); // Urdu f
        unicode.put("\u095C","δ"); // Hindi 'dh' as in padh
        unicode.put("\u095D","Δ"); // hindi dhh*/
        unicode!!["\u0926\u093C"] = "τ" // Urdu dwad
        unicode!!["\u0924\u093C"] = "θ" // Urdu toe
        unicode!!["\u0938\u093C"] = "σ" // Urdu swad, se
    }

    fun transform(s1: String): String {
        val transformed = StringBuilder()
        val strLen = s1.length
        var shabda: ArrayList<String?>? = ArrayList()
        var lastEntry: String? = ""
        for (i in 0 until strLen) {
            val c = s1[i]
            val varna = c.toString()
            Log.e(TAG, "transform: $varna\n")
            val halant = "0x0951"
            if (VowelUtil.isConsonant(varna)) {
                Log.e(TAG, "transform: " + unicode!![varna])
                shabda!!.add(unicode!![varna])
                shabda.add(halant) //halant
                lastEntry = halant
            } else if (VowelUtil.isVowel(varna)) {
                Log.e(TAG, "transform: " + "Vowel Detected...")
                if (halant == lastEntry) {
                    if (varna == "a") {
                        shabda!![shabda.size - 1] = ""
                    } else {
                        shabda!![shabda.size - 1] = unicode!![varna]
                    }
                } else {
                    shabda!!.add(unicode!![varna])
                }
                lastEntry = unicode!![varna]
            } // end of else if is-Vowel
            else if (unicode!!.containsKey(varna)) {
                shabda!!.add(unicode!![varna])
                lastEntry = unicode!![varna]
            } else {
                shabda!!.add(varna)
                lastEntry = varna
            }
        } // end of for
        for (string in shabda!!) {
            transformed.append(string)
        }

        //Discard the shabda array
        shabda = null
        return transformed.toString() // return transformed;
    }

    init {
        populateHashTable()
    }
}