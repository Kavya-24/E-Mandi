package com.example.mandiexe.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.mandiexe.R
import com.example.mandiexe.models.ProfileObject
import com.example.mandiexe.models.body.AddressBlock
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.body.authBody.SignUpBody
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.models.responses.auth.SignUpResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.LocationHelper
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.viewmodels.MapViewmodel
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.map_fragment.*
import java.util.*

class MapActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = MapActivity()
    }


    private var supportMapFragment = SupportMapFragment()
    private lateinit var client: FusedLocationProviderClient
    private val permissionRequestCode = 1234
    private val RESULT_OK = 111
    private val TAG = MapActivity::class.java.simpleName
    private lateinit var fab: FloatingActionButton
    private var fetchedLocation: String = ""

    private val preferenceManager: PreferenceManager = PreferenceManager()
    private val sessionManager: SessionManager = SessionManager(ApplicationUtils.getContext())

    private val pref = PreferenceUtil
    private var fetchedAddress: Address = Address(Locale(pref.getLanguageFromPreference()!!))
    private var fetchedEnglishAddress = Address(Locale("en"))

    private var body = SignUpBody("", 0, "", "", "", "", "", "", "", "", "", "")
    private var args: Bundle? = null

    private var fromSignUp = false

    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder

    private val viewModel: MapViewmodel by viewModels()
    private val viewmodelLogin: OTViewModel by viewModels()

    private lateinit var pb: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.map_fragment)

        setTitle(R.string.location)

        args = intent?.getBundleExtra("bundle")

        if (args != null) {
            //Then this is from signUp
            fromSignUp = true
        }

        fab = findViewById(R.id.fav_check_map)


        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        client = this.let { LocationServices.getFusedLocationProviderClient(it) }

        getPermissions()

        fab.setOnClickListener {
            if (fromSignUp) {
                createDialog()
            }
            //Get the location
            else {
                onBackPressed()
            }
        }


    }

    private fun createDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.layout_new_user_confirmation, null)
        d.setView(v)


        val tvName = v.findViewById<TextView>(R.id.tv_new_user_name)
        val tvAddress = v.findViewById<TextView>(R.id.tv_new_user_address)

        tvName.text = args?.getString("NAME")
        if (fetchedLocation == "" || fetchedLocation == resources.getString(R.string.NotFound)) {
            tvAddress.text = args?.getString("ADDRESS_USER")
        } else {
            tvAddress.text =
                fetchedLocation        //This is the address that we have got from the map
        }


        //Positive and negative buttons

        //Create observer on Text

        d.setPositiveButton(resources.getString(R.string.register)) { md, _ ->
            md.dismiss()
            createUser()
        }

        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        d.show()


    }

    private fun createUser() {
        //Create a new user

        pb = ProgressDialog(this)
        pb.setMessage(resources.getString(R.string.creatinguser))
        pb.show()

        try {


            //All body in English
            val mCountry = fetchedEnglishAddress.countryName
            val mDistrict = fetchedEnglishAddress.subAdminArea
            val mState = fetchedEnglishAddress.adminArea
            val village = args?.getString("ADDRESS_USER")!!
            val mAddress = "$village,$mDistrict"
            val lat = fetchedEnglishAddress.latitude.toString()
            val long = fetchedEnglishAddress.longitude.toString()
            val token = args?.getString("TOKEN")
            val name = args?.getString("NAME").toString()
            val area = args?.getString("AREA").toString().toInt()
            val area_unit = args?.getString("AREA_UNIT").toString()
            val phone = args?.getString("PHONE").toString()

            //val phone = args?.getString("PHONE")!!.drop(2).toString()

            body = SignUpBody(
                mAddress,
                area,
                area_unit,
                mCountry,
                mDistrict,
                lat,
                long,
                name,
                phone,
                mState,
                token!!,
                village
            )

            makeMapCall(body)


        } catch (e: java.lang.Exception) {
            //Case when we are not able to get the location of the person

            val myCurrentAddress = getMyExactLocationWhileError()

            UIUtils.logExceptions(e, TAG)

            val mCountry = myCurrentAddress.countryName
            val mDistrict = myCurrentAddress.subAdminArea
            val mState = myCurrentAddress.adminArea
            val village = args?.getString("ADDRESS_USER")!!
            val mAddress = "$village,$mDistrict"
            val lat = myCurrentAddress.latitude.toString()
            val long = myCurrentAddress.longitude.toString()
            val token = args?.getString("TOKEN")
            val name = args?.getString("NAME").toString()
            val area = args?.getString("AREA").toString().toInt()
            val area_unit = args?.getString("AREA_UNIT").toString()
            val phone = args?.getString("PHONE").toString()

            //val phone = args?.getString("PHONE")!!.drop(2).toString()

            body = SignUpBody(
                mAddress,
                area,
                area_unit,
                mCountry,
                mDistrict,
                lat,
                long,
                name,
                phone,
                mState,
                token!!,
                village
            )

            makeMapCall(body)

        }
    }


    private fun makeMapCall(body: SignUpBody) {
        val mView = findViewById<ConstraintLayout>(R.id.container_map)
        viewModel.signFunction(body, mView, pb).observe(this, Observer { mResponse ->
            val success = viewModel.successful.value
            if (success != null) {
                pb.dismiss()
                manageSignUpResponse(viewModel.mSignUp.value)

            } else {
                Log.e(TAG, "Loading.......")
                pb.show()
            }

        })
    }

    private fun manageSignUpResponse(value: SignUpResponse?) {
        Log.e(TAG, "In manageSignUp resonse with value")
        if (value != null) {

            Log.e(TAG, "In manage signup response with response as $value")

            if (value.msg == "Registeration successful.") {

                createSnackbar(
                    resources.getString(R.string.signUpSuccess),
                    this@MapActivity,
                    container_map
                )
                signUpSuccess(value)

            } else {

                loginFromSignUp()

            }
        } else {
            createSnackbar(resources.getString(R.string.failedLogin), this, container_map)
        }
    }

    private fun signUpSuccess(response: SignUpResponse) {

        //Set the preferneces here

        //Set phone number
        pref.setNumberFromPreference(body.phone)

        //Set Address block
        val addressBlock = AddressBlock(
            body.district,
            body.village,
            body.state,
            body.country,
            body.address,
            body.latitude,
            body.longitude
        )

        pref.setAddressFromPreference(addressBlock)

        //Set Area prefernce
        pref.setAreaUnitFromPreference(body.area_unit)


        //Set ProfileObject
        val profileObject =
            ProfileObject(body.name, body.area_numerical.toString(), body.area_unit, addressBlock)
        pref.setProfile(profileObject)

        loginFromSignUp()


    }

    private fun loginFromSignUp() {

        val token = args?.getString("TOKEN")
        val body = LoginBody(token!!)

        Log.e(TAG, "Firebase Token " + token)


//        viewmodelLogin.lgnFunction(body, pb, mSnackbarView).observe(this, Observer { mResponse ->
//
//
//            val success = viewmodelLogin.successful.value
//            if (success != null) {
//                if (success == false) {
//                    createSnackbar(viewmodelLogin.message.value, this, container_map)
//                } else {
//
//
//                    manageLoginResponse(viewmodelLogin.mLogin.value, token)
//                }
//            } else {
//                createSnackbar(resources.getString(R.string.failedLogin), this, container_map)
//            }
//
//        })
//

    }

    private fun manageLoginResponse(mResponse: LoginResponse?, token: String) {

        if (mResponse != null) {
            if (mResponse.msg == "Login successful.") {

                //Set the user details
                successLogin(mResponse)
                Log.e(TAG, "In manage login and login succcess")

            }
        } else {

            createSnackbar(resources.getString(R.string.failedLogin), this, container_map)
        }
    }

    private fun successLogin(response: LoginResponse) {
        //Set access tokens
        Log.e(TAG, "Success Login and response is " + response.toString())

        response.user?.accessToken?.let { sessionManager.saveAuth_access_Token(it) }

        //response?.user?.refreshToken?.let { sessionManager.saveAuth_refresh_Token(it) }
        response.user?.refreshToken?.let { preferenceManager.putAuthToken(it) }

        Log.e(TAG, "AT: \n" + sessionManager.fetchAcessToken().toString())
        //Log.e(TAG, "RT: \n" + sessionManager.fetchRefreshToken().toString())
        Log.e(TAG, "PT: \n" + preferenceManager.authToken.toString())

        response.user?.phone?.let { pref.setNumberFromPreference(it) }
        pref.name = response.user?.name


        Toast.makeText(this, resources.getString(R.string.loginSuceed), Toast.LENGTH_LONG)
            .show()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        UIUtils.hideKeyboard(this, this)
        startActivity(intent)
        finish()

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this as Activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionRequestCode
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionRequestCode ->
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show()

                } else {
                    getLocationInMap()

                }

        }

        //Grant results 0; for fine locations

    }

    private fun getPermissions() {

        Log.e(TAG, "In get permissions")

        val p = this.let {
            this.let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }


        if (p == PackageManager.PERMISSION_GRANTED) {
            getLocationInMap()
        }

        if (p != PackageManager.PERMISSION_GRANTED) {
            //Not permitted
            Toast.makeText(
                this,
                resources.getString(R.string.permissionRequired),
                Toast.LENGTH_SHORT
            ).show()


        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //Tell user what we are going to do with this permission
            val b = AlertDialog.Builder(this)
            b.setMessage(resources.getString(R.string.permissionMessageLocation))
            b.setTitle(resources.getString(R.string.permissionRequired))
            b.setPositiveButton(resources.getString(R.string.allow)) { _: DialogInterface?, _: Int ->
                makeRequest()
            }
            val dialog = b.create()
            dialog.show()
        } else {
            makeRequest()
        }

    }

    private fun getLocationInMap() {

        val myCurrentLatLong = getMarkerForCurrentPosition()

        val task: Task<Location> = client.lastLocation

        task.addOnSuccessListener { mLocation ->

            if (mLocation != null) {
                supportMapFragment.getMapAsync { gMap ->


                    //Set Up the marker
                    val marker = myCurrentLatLong?.let {
                        MarkerOptions().position(it)
                            .title(resources.getString(R.string.you_are_here))
                    }

                    val zoomLevel = 16.0f //This goes up to 21
                    gMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            myCurrentLatLong,
                            zoomLevel
                        )
                    )

                    if (gMap != null) {
                        gMap.addMarker(marker)
                    }



                    gMap?.setOnMapClickListener { newLatLong ->
                        //Create a new marker

                        gMap.clear()
                        val newmarker = myCurrentLatLong?.let {
                            MarkerOptions().position(it)
                                .title(resources.getString(R.string.you_are_here))
                        }

                        gMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                myCurrentLatLong,
                                zoomLevel
                            )
                        )

                        gMap.addMarker(newmarker)

                        fetchedLocation = getAddress(newLatLong)
                        getEnglishAddress(newLatLong)
                        createSnackbar(fetchedLocation, this@MapActivity, container_map)


                    }
                }
            } else {
                fetchedLocation = resources.getString(R.string.NotFound)
            }
        }

        task.addOnFailureListener { excetion ->
            Log.e(TAG, "Failed to load maps, willing to reexecute")
            //Get permisions again
            getPermissions()
        }


    }

    private fun getMarkerForCurrentPosition(): LatLng? {

        val myCurrentLocation = LocationHelper(this).getLocation(this)
        val myCurrentLatLong =
            myCurrentLocation?.longitude?.let { LatLng(myCurrentLocation.latitude, it) }
        fetchedLocation = myCurrentLatLong?.let { getAddress(it) }.toString()
        if (myCurrentLatLong != null) {
            getEnglishAddress(myCurrentLatLong)
        }
        Log.e(TAG, "The current location is given by $fetchedLocation")

        return myCurrentLatLong

    }

    private fun getMyExactLocationWhileError(): Address {

        val myCurrentLocation = LocationHelper(this).getLocation(this)
        val myCurrentLatLog =
            myCurrentLocation?.latitude?.let { LatLng(it, myCurrentLocation.longitude) }
        val locale = Locale("en")
        var myCurrentAddress = Address(Locale("en"))
        val geocoder = Geocoder(this, locale)
        try {
            val addresses: List<Address> =
                myCurrentLatLog?.latitude?.let {
                    geocoder.getFromLocation(
                        it,
                        myCurrentLatLog.longitude,
                        1
                    )
                } as List<Address>
            val mAddress = addresses.get(0).getAddressLine(0)
            Log.e(TAG, mAddress.toString())
            myCurrentAddress = addresses.get(0)

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }

        return myCurrentAddress
    }


    private fun getAddress(latLang: LatLng): String {
        var theAddress = ""

        //##Get location
        val locale = Locale(pref.getLanguageFromPreference().toString())

        val geocoder = Geocoder(this, locale)
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLang.latitude, latLang.longitude, 1)
            val mAddress = addresses.get(0).getAddressLine(0)
            Log.e(TAG, mAddress.toString())
            theAddress = mAddress
            fetchedAddress = addresses.get(0)

        } catch (e: Exception) {
            return e.message.toString()
        }


        return theAddress
    }

    private fun getEnglishAddress(latLang: LatLng) {

        var theAddress = ""

        //##Get location
        val locale = Locale("en")

        val geocoder = Geocoder(this, locale)
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLang.latitude, latLang.longitude, 1)
            val mAddress = addresses.get(0).getAddressLine(0)
            Log.e(TAG, mAddress.toString())
            theAddress = mAddress
            fetchedEnglishAddress = addresses.get(0)

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }


    }

    override fun onBackPressed() {

        //This result is when it comes to another activty or fragment
        val bundle = Bundle()

        bundle.putString("fetchedLocation", fetchedLocation)
        Log.e(TAG, "In back pressed and address is " + fetchedLocation.toString())
        val mIntent = Intent()
        mIntent.putExtras(bundle)
        setResult(RESULT_OK, mIntent)
        super.onBackPressed()
    }

}

