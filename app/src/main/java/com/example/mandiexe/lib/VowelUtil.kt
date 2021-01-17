package com.example.mandiexe.lib

object VowelUtil {
    internal fun isVowel(strVowel: String): Boolean {
        // Log.logInfo("came in is_Vowel: Checking whether string is a Vowel");
        return strVowel == "a" || strVowel == "aa" || strVowel == "i" || strVowel == "ee" || strVowel == "u" || strVowel == "oo" || strVowel == "ri" || strVowel == "lri" || strVowel == "e" || strVowel == "ai" || strVowel == "o" || strVowel == "au" || strVowel == "om"
    }

    internal fun isConsonant(strConsonant: String): Boolean {
        // Log.logInfo("came in is_consonant: Checking whether string is a
        // consonant");
        return strConsonant == "k" || strConsonant == "kh" || strConsonant == "g" || strConsonant == "gh" || strConsonant == "ng" || strConsonant == "ch" || strConsonant == "chh" || strConsonant == "j" || strConsonant == "jh" || strConsonant == "ny" || strConsonant == "t" || strConsonant == "th" || strConsonant == "d" || strConsonant == "dh" || strConsonant == "n" || strConsonant == "nn" || strConsonant == "p" || strConsonant == "ph" || strConsonant == "b" || strConsonant == "bh" || strConsonant == "m" || strConsonant == "y" || strConsonant == "r" || strConsonant == "rr" || strConsonant == "l" || strConsonant == "ll" || strConsonant == "lll" || strConsonant == "v" || strConsonant == "sh" || strConsonant == "ss" || strConsonant == "s" || strConsonant == "h" || strConsonant == "3" || strConsonant == "z" || strConsonant == "v" || strConsonant == "Ω" || strConsonant == "κ" || strConsonant == "K" || strConsonant == "γ" || strConsonant == "ζ" || strConsonant == "φ" || strConsonant == "δ" || strConsonant == "Δ" || strConsonant == "τ" || strConsonant == "θ" || strConsonant == "σ"
    }
}