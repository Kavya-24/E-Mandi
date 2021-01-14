package com.example.mandiexe.utils.auth

import android.preference.PreferenceManager
import com.example.mandiexe.R
import com.example.mandiexe.models.ProfileObject
import com.example.mandiexe.models.body.AddressBlock
import com.example.mandiexe.utils.ApplicationUtils


/**
 * Preference Util to save the data locally in shared preference
 */
object PreferenceUtil {


    private val pm = PreferenceManager.getDefaultSharedPreferences(ApplicationUtils.instance)

    private const val ID = "_id"

    private const val NAME = "name"
    private val AREA_UNIT = "area_unit"
    private val AREA = "area"

    //Address
    private val VILLAGE = "city"
    private val DISTRICT = "city"
    private val STATE = "city"
    private val COUNTRY = "city"
    private val ADDRESS = "address"
    private val LATITUDE = "latitude"
    private val LONGITUDE = "longitude"


    private val PHONE = "phone"
    private const val FIREBASE_UID = "fuid"
    private val LANGUAGE = "language"

    private val QUANTITY_UNIT = "quantity_unit"
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

    var area: String?
        get() = pm.getString(AREA, " ")
        set(value) {
            pm.edit().putString(AREA, value).apply()
        }

    var area_unit: String?
        get() = pm.getString(
            AREA_UNIT,
            ApplicationUtils.getContext().resources.getString(R.string.bigha)
        )
        set(value) {
            pm.edit().putString(AREA_UNIT, value).apply()
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

    var fuid: String?
        get() = pm.getString(FIREBASE_UID, " ")
        set(value) {
            pm.edit().putString(FIREBASE_UID, value).apply()
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

    //Address Units
    var village: String?
        get() = pm.getString(VILLAGE, " ")
        set(value) {
            pm.edit().putString(VILLAGE, value).apply()
        }

    var district: String?
        get() = pm.getString(DISTRICT, " ")
        set(value) {
            pm.edit().putString(DISTRICT, value).apply()
        }

    var state: String?
        get() = pm.getString(STATE, " ")
        set(value) {
            pm.edit().putString(STATE, value).apply()
        }

    var country: String?
        get() = pm.getString(COUNTRY, " ")
        set(value) {
            pm.edit().putString(COUNTRY, value).apply()
        }

    var address: String?
        get() = pm.getString(ADDRESS, " ")
        set(value) {
            pm.edit().putString(ADDRESS, value).apply()
        }


    var latitude: String?
        get() = pm.getString(LATITUDE, " ")
        set(value) {
            pm.edit().putString(LATITUDE, value).apply()
        }

    var longitude: String?
        get() = pm.getString(LONGITUDE, " ")
        set(value) {
            pm.edit().putString(LONGITUDE, value).apply()
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
        pref.area_unit = aUnit
    }

    fun getAreaUnitFromPreference(): String? {
        val pref = PreferenceUtil
        return pref.area_unit
    }


    /**
    Quantity Preference
     */
    fun setQuanitityUnitFromPreference(qUnit: String) {
        val pref = PreferenceUtil
        pref.quantity_unit = qUnit
    }

    fun getQuanitityUnitFromPreference(): String? {
        val pref = PreferenceUtil
        return (pref.quantity_unit)
    }


    /**
     * Firebase uid
     */
    fun setFUIDFromPreference(fid: String) {
        val pref = PreferenceUtil
        pref.fuid = fid
    }

    fun getFUIDFromPreference(): String? {
        val pref = PreferenceUtil
        return pref.fuid
    }


    /**
     * Phone number
     */
    fun setNumberFromPreference(ph: String) {
        val pref = PreferenceUtil
        pref.phone = ph
    }

    fun getNumberFromPreference(): String? {
        val pref = PreferenceUtil
        return pref.phone
    }

    /**
     * Set Address
     */

    fun setAddressFromPreference(body: AddressBlock) {
        val pref = PreferenceUtil
        pref.village = body.village
        pref.district = body.district
        pref.state = body.state
        pref.country = body.country
        pref.address = body.address
        pref.latitude = body.latitude
        pref.longitude = body.longitude

    }

    fun getAddressFromPreference(): AddressBlock? {
        val pref = PreferenceUtil
        return AddressBlock(
            pref.village!!,
            pref.district!!,
            pref.state!!,
            pref.country!!,
            pref.address!!,
            pref.latitude!!,
            pref.longitude!!

        )
    }

    /**
    Set Profile
     */

    fun setProfile(profile: ProfileObject) {
        val pref = PreferenceUtil
        pref.name = profile.name
        pref.area_unit = profile.area_unit
        pref.area = profile.area

        setAddressFromPreference(profile.addressBlock)
    }

    fun getProfile(): ProfileObject {
        val pref = PreferenceUtil
        return ProfileObject(
            pref.name!!,
            pref.area!!,
            pref.area_unit!!,
            getAddressFromPreference()!!
        )
    }

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