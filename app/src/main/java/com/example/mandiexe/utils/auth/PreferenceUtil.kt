package com.example.mandiexe.utils.auth

import android.preference.PreferenceManager
import com.example.mandiexe.utils.ApplicationUtils


/**
 * Preference Util to save the data locally in shared preference
 */
object PreferenceUtil {


    private val pm = PreferenceManager.getDefaultSharedPreferences(ApplicationUtils.instance)

    private const val ID = "_id"
    private const val NAME = "name"
    private val PHONE = "phone"

    private val AREA_UNIT = "area_unit"
    private val QUANTITY_UNIT = "quantity_unit"
    private val LANGUAGE = "language"

    private val HAS_SEEN_WALKTHROUGH = "hasSeenWalkthrough"


    var _id: String?
        get() = pm.getString(ID, "")
        set(value) {
            pm.edit().putString(ID, value).apply()
        }

    var name: String?
        get() = pm.getString(NAME, "")
        set(value) {
            pm.edit().putString(NAME, value).apply()
        }

    var language: String?
        get() = pm.getString(LANGUAGE, "en")
        set(value) {
            pm.edit().putString(LANGUAGE, value).apply()
        }

    var phone: String?
        get() = pm.getString(PHONE, "0")
        set(value) {
            pm.edit().putString(PHONE, value).apply()
        }

    var area_unit: String?
        get() = pm.getString(PHONE, "bigha")
        set(value) {
            pm.edit().putString(PHONE, value).apply()
        }


    var quantity_unit: String?
        get() = pm.getString(QUANTITY_UNIT, "kg")
        set(value) {
            pm.edit().putString(QUANTITY_UNIT, value).apply()
        }



    var hasSeenWalkthrough: Boolean
        get() = pm.getBoolean(HAS_SEEN_WALKTHROUGH, false)
        set(value) {
            pm.edit().putBoolean(HAS_SEEN_WALKTHROUGH, value).apply()
        }

    /**
    Language Preference
     */
    fun setLanguageFromPreference(mLocale: String) {
        val pref = PreferenceUtil
        pref.language = mLocale
    }

    fun getLanguageFromPreference(): String? {
        val pref = PreferenceUtil
        return (pref.language)
    }


    /**
    Area Preference
     */
    fun setAreaUnitFromPreference(aUnit: String) {
        val pref = PreferenceUtil
        pref.language = aUnit
    }

    fun getAreaUnitFromPreference(): String? {
        val pref = PreferenceUtil
        return (pref.area_unit)
    }


    /**
    Quantity Preference
     */
    fun setQuanitityUnitFromPreference(qUnit: String) {
        val pref = PreferenceUtil
        pref.language = qUnit
    }

    fun getQuanitityUnitFromPreference(): String? {
        val pref = PreferenceUtil
        return (pref.quantity_unit)
    }



    /*fun setUserFromPreference(user: ProfileResponse.Profile) {
        //Instamce of pref util
        val pref = PreferenceUtil
        pref._id = user._id
        pref.email = user.email
        pref.username = user.username
        pref.name = user.name
    }


    fun getUserFromPrefernece(): ProfileResponse.Profile {
        val prefUtil = PreferenceUtil
        return ProfileResponse.Profile(
            prefUtil._id!!,
            prefUtil.name!!,
            prefUtil.
        )

    }

*/


    fun clearPrefData() {
        pm.edit().clear().apply()
    }

    fun getWalkthrough(): Boolean {
        val p = PreferenceUtil
        return p.hasSeenWalkthrough
    }

    fun setWalkthroughStatus(hasSeen: Boolean) {
        val pref = PreferenceUtil
        pref.hasSeenWalkthrough = hasSeen
    }

}