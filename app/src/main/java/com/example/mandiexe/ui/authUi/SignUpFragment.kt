package com.example.mandiexe.ui.authUi

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mandiexe.R
import com.example.mandiexe.models.body.AddressBlock
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.body.authBody.SignUpBody
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.models.responses.auth.SignUpResponse
import com.example.mandiexe.ui.home.MainActivity
import com.example.mandiexe.ui.home.MapActivity
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.LocationHelper
import com.example.mandiexe.utils.PermissionsHelper
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.getAddress
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.MapViewmodel
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.sign_up_fragment.*
import java.util.*
import java.util.concurrent.TimeUnit

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var root: View
    private val TAG = SignUpFragment::class.java.simpleName

    //UI elements
    private lateinit var tilName: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilArea: TextInputLayout
    private lateinit var tilAUnit: TextInputLayout


    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etArea: EditText
    private lateinit var etAreaUnit: AutoCompleteTextView

    private val RC_MAP_SIGNUP = 111

    private var supportMapFragment = SupportMapFragment()
    private lateinit var client: FusedLocationProviderClient
    private val permissionRequestCode = 1234
    private val RESULT_OK = 111


    //A special code to tell it that this is from SignUp and it need to create dialog
    private val mapFromSignUp = "10"
    private val pref = PreferenceUtil

    private var TOKEN = ""
    private var PHONE = ""

    private lateinit var pb_sign_main: ProgressBar
    private lateinit var pb_sign_add: ProgressBar
    private lateinit var tvAddress: TextView

    private val viewModelSignup: MapViewmodel by viewModels()
    private val viewModelLogin: OTViewModel by viewModels()

    private var fetchedLocation = ""
    private var fetchedEnglishAddress = Address(Locale("en"))

    private val preferenceManager: PreferenceManager = PreferenceManager()
    private val sessionManager: SessionManager = SessionManager(ApplicationUtils.getContext())

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.sign_up_fragment, container, false)

        //Ui Init
        tilName = root.findViewById(R.id.tilUSerName)
        tilAddress = root.findViewById(R.id.tilUserAddress)
        tilAUnit = root.findViewById(R.id.tilAUnit)
        tilArea = root.findViewById(R.id.tilArea)

        etName = root.findViewById(R.id.etUserName)
        etAddress = root.findViewById(R.id.etUserAddress)
        etArea = root.findViewById(R.id.etArea)
        etAreaUnit = root.findViewById(R.id.actv_area_unit)

        pb_sign_add = root.findViewById(R.id.pb_sig_address)
        pb_sign_main = root.findViewById(R.id.pb_sig_up_main)
        tvAddress = root.findViewById(R.id.tv_address_fetched)

        Log.e(TAG, " In befire")

//        mapView = root.findViewById(R.id.map_view)
//        mapView.getMapAsync { p0 ->
//            Log.e(TAG, "In on map main ready")
//            MapReady(p0)
//        }


        Log.e(TAG, "In afert")



        if (arguments != null) {
            TOKEN = requireArguments().getString("TOKEN").toString()
            PHONE = requireArguments().getString("PHONE").toString()
        }


        client = LocationServices.getFusedLocationProviderClient(requireContext())

        //Populate units
        populateAreaUnit()


        root.findViewById<MaterialButton>(R.id.mtb_sign_up).setOnClickListener {
            if (isValidate()) {
                //valid details
                //Save the prefered unit
                pref.setAreaUnitFromPreference(etArea.text.toString())
                //goToMapActivity()
                createDialog()
            }
        }


        return root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createDialog() {

        val d = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val v = layoutInflater.inflate(R.layout.layout_new_user_confirmation, null)
        d.setView(v)


        val tvName = v.findViewById<TextView>(R.id.tv_new_user_name)
        val tvAddress = v.findViewById<TextView>(R.id.tv_new_user_address)

        tvName.text = etName.text.toString()

        if (fetchedLocation == "" || fetchedLocation == resources.getString(R.string.NotFound) || fetchedLocation.isEmpty()) {
            tvAddress.text = etAddress.text.toString()
        } else {
            tvAddress.text =
                fetchedLocation        //This is the address that we have got from the map
        }


        d.setPositiveButton(resources.getString(R.string.register)) { md, _ ->
            md.dismiss()
            createUser()
        }

        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        d.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createUser() {


        //Transliterate Name
        val name = OfflineTranslate.transliterateToEnglish(etName.text.toString())
            ?: etName.text.toString()
        val phone = PHONE
        val token = TOKEN
        val area = etArea.text.toString().toInt()
        val arUnit = etAreaUnit.text.toString()

        try {


            //All body in English
            val mCountry = fetchedEnglishAddress.countryName
            val mDistrict = fetchedEnglishAddress.subAdminArea
            val mState = fetchedEnglishAddress.adminArea
            val village = fetchedEnglishAddress.locality
            val mAddress = "$village,$mDistrict"

            val lat = fetchedEnglishAddress.latitude.toString()
            val long = fetchedEnglishAddress.longitude.toString()


            //val phone = args?.getString("PHONE")!!.drop(2).toString()

            val body = SignUpBody(
                mAddress,
                area_numerical = etArea.text.toString().toInt(),
                area_unit = etAreaUnit.text.toString(),
                mCountry,
                mDistrict,
                lat,
                long,
                name,
                phone,
                mState,
                token,
                village
            )

            makeMapCall(body)


        } catch (e: java.lang.Exception) {
            //Case when we are not able to get the location of the person
            //Case when we are not able to get from network and gos location
            UIUtils.logExceptions(e, TAG)
            createSnackbar(
                resources.getString(R.string.locationNotDetected),
                requireContext(),
                container_sign_up
            )

            val mCountry = resources.getString(R.string.nf)
            val mDistrict = resources.getString(R.string.nf)
            val mState = resources.getString(R.string.nf)
            val village = resources.getString(R.string.nf)
            val mAddress = resources.getString(R.string.nf)

            val lat = "0.0"
            val long = "0.0"

            val body = SignUpBody(
                mAddress,
                area,
                arUnit,
                mCountry,
                mDistrict,
                lat,
                long,
                name,
                phone,
                mState,
                token,
                village
            )

            makeMapCall(body)

        }

    }

    private fun makeMapCall(body: SignUpBody) {

        val mView = root.findViewById<ConstraintLayout>(R.id.container_sign_up)

        viewModelSignup.signFunction(body, mView, pb_sign_main)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { mResponse ->
                val success = viewModelSignup.successful.value
                if (success != null) {
                    hideProgress(pb_sign_main, requireContext())
                    manageSignUpResponse(viewModelSignup.mSignUp.value, body)

                } else {
                    Log.e(TAG, "Loading.......")
                    hideProgress(pb_sign_main, requireContext())
                }

            })
    }

    private fun manageSignUpResponse(value: SignUpResponse?, body: SignUpBody) {

        Log.e(TAG, "In manageSignUp resonse with value")
        if (value != null) {

            Log.e(TAG, "In manage signup response with response as $value")

            if (value.msg == "Registeration successful.") {

                createSnackbar(
                    resources.getString(R.string.signUpSuccess),
                    requireContext(),
                    container_sign_up
                )
                signUpSuccess(value, body)

            } else {

                loginFromSignUp()

            }
        } else {
            createSnackbar(
                resources.getString(R.string.failedLogin),
                requireContext(),
                container_sign_up
            )
        }
    }

    private fun signUpSuccess(value: SignUpResponse, body: SignUpBody) {
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

        loginFromSignUp()
    }

    private fun loginFromSignUp() {

        val body = LoginBody(TOKEN)

        Log.e(TAG, "Firebase Token " + TOKEN)

        val mSnackbarView = root.findViewById<ConstraintLayout>(R.id.container_sign_up)
        viewModelLogin.lgnFunction(body, pb_sign_main, mSnackbarView)
            .observe(viewLifecycleOwner, Observer { mResponse ->


                val success = viewModelLogin.successful.value
                if (success != null) {
                    hideProgress(pb_sign_main, requireContext())
                    if (success == false) {
                        createSnackbar(
                            viewModelLogin.message.value,
                            requireContext(),
                            container_sign_up
                        )

                    } else {


                        manageLoginResponse(viewModelLogin.mLogin.value, TOKEN)
                    }
                } else {
                    showProgress(pb_sign_main, requireContext())
//                    createSnackbar(
//                        resources.getString(R.string.failedLogin),
//                        requireContext(),
//                        container_signUp
//                    )
                }

            })


    }

    private fun manageLoginResponse(mResponse: LoginResponse?, token: String) {
        if (mResponse != null) {
            if (mResponse.msg == "Login successful.") {

                //Set the user details
                successLogin(mResponse)
                Log.e(TAG, "In manage login and login succcess")

            }
        } else {

            createSnackbar(
                resources.getString(R.string.failedLogin),
                requireContext(),
                container_sign_up
            )
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


        Toast.makeText(
            requireContext(),
            resources.getString(R.string.loginSuceed),
            Toast.LENGTH_LONG
        )
            .show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        UIUtils.hideKeyboard(context as Activity, requireContext())
        startActivity(intent)
        onDestroy()
        activity?.finish()

    }

    private fun getAutocorrectLocation() {

        //Gets the location and sets in Map
        checkSelfPermsissions()

    }

    private fun checkSelfPermsissions() {

        //Request for storage permissions then start camera
        PermissionsHelper.requestMapsPermissions(requireActivity())
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { callback ->
                    if (callback) {
                        //Done
                        launchMaps()
                    }
                },
                onError = {
                    Log.e(TAG, "Error for permissions and ${it.message} ${it.cause}")
                },
                onComplete = {
                    Log.e(TAG, "In complete")
                }
            )

    }

    private fun launchMaps() {

        showProgress(pb_sign_add, requireContext())
        val myCurrentLocation = context?.let { LocationHelper(it).getLocation(requireContext()) }
        currentLatLng = myCurrentLocation?.latitude?.let { LatLng(it, myCurrentLocation.longitude) }

        if (currentLatLng != null) {


            //Get the default address and set in the editable text box

            val myAddressInLocale = ExternalUtils.getAddress(
                requireContext(),
                pref.getLanguageFromPreference() ?: "en",
                currentLatLng!!,
            )


            fetchedLocation = myAddressInLocale.getAddressLine(0)
            fetchedEnglishAddress =
                ExternalUtils.getAddress(requireContext(), "en", currentLatLng!!)

            tvAddress.text = myAddressInLocale.getAddressLine(0)
            //etAddress.setText(myAddressInLocale.getAddressLine(0), TextView.BufferType.EDITABLE)

            //Use current latlong in the map

        } else {
            Log.e(TAG, "Null location from fused location provider")
        }



        hideProgress(pb_sign_add, requireContext())
    }


    private fun errorInCreatingMaps(currentLatLng: LatLng?) {

        showProgress(pb_sign_add, requireContext())
        createSnackbar(
            resources.getString(R.string.notAbleToCreateGMap),
            requireContext(),
            container_sign_up
        )
        root.findViewById<MapView>(R.id.map_view).visibility = View.GONE

        if (currentLatLng != null) {

            //Without Marker
            fetchedLocation = getAddress(
                requireContext(),
                pref.getLanguageFromPreference() ?: "en",
                currentLatLng
            ).getAddressLine(0)
            fetchedEnglishAddress =
                ExternalUtils.getAddress(requireContext(), "en", currentLatLng)
            createSnackbar(fetchedLocation, requireContext(), container_sign_up)
            //Set in the tecxt view
            tvAddress.text = fetchedLocation

        } else {
            fetchedLocation = "ADDRESS_NOT_FOUND"

        }

        hideProgress(pb_sign_add, requireContext())

    }

    private fun populateAreaUnit() {

        etAreaUnit.setText(resources.getString(R.string.bigha))
        val array: Array<String> = resources.getStringArray(R.array.arr_area_units)
        val adapter: ArrayAdapter<String>? = context?.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item,
                array
            )
        }
        etAreaUnit.setAdapter(adapter)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun goToMapActivity() {

        val i = Intent(requireContext(), MapActivity::class.java)


        val b = bundleOf(
            "TOKEN" to TOKEN,
            "PHONE" to PHONE,
            "RC" to mapFromSignUp,                          //For the thing that it goes from Login to Map Activity
            "NAME" to OfflineTranslate.transliterateToEnglish(etName.text.toString()).toString()
                .capitalize(Locale.ROOT),//(T)
            "AREA" to etArea.text.toString(),
            "AREA_UNIT" to etAreaUnit.text.toString(),
            "ADDRESS_USER" to OfflineTranslate.transliterateToEnglish(etAddress.text.toString())
                .toString().capitalize(Locale.ROOT)//(T)         //This is the village
        )

        Log.e("SignUp ", "Bundle passed is " + b.toString())

        i.putExtra("bundle", b)
        Log.e("SIGN", PreferenceUtil.getLanguageFromPreference().toString())
        startActivityForResult(i, RC_MAP_SIGNUP)
    }

    private fun isValidate(): Boolean {
        var isValid = true

        when {
            etName.text.isEmpty() -> {
                isValid = false
                tilName.error = resources.getString(R.string.emptyName)
            }
            !ValidationObject.validateName(etName.text.toString()) -> {
                isValid = false
                tilName.error = resources.getString(R.string.invalidName)
            }
            else -> {
                tilName.error = null
            }
        }


        if (etArea.text.isEmpty() || etArea.text.toString() == "0") {
            isValid = false
            tilArea.error = resources.getString(R.string.emptyAreaError)
        } else {
            tilArea.error = null
        }


        if (etAddress.text.isEmpty() || etAddress.text.toString() == "null") {
            isValid = false
            tilAddress.error = resources.getString(R.string.addressError)
        } else {
            tilAddress.error = null
        }


        return isValid
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {

        viewModelSignup.message.value = null
        viewModelSignup.successful.value = null

        viewModelLogin.successful.value = null
        viewModelLogin.message.value = null

        //Destroy map
        super.onDestroy()

    }

    private fun MapReady(gMap: GoogleMap?) {
        val task: Task<Location> = client.lastLocation
        val zoomLevel = 20.0f //This goes up to 21
        mapView.visibility = View.VISIBLE

        Log.e(TAG, "In map ready")
        if (gMap != null) {
            Log.e(TAG, "gmap not null")

            //Get the current location
            //This inflates the fetchedLocation and fetchedEnglishAddress and currentLatLng
            checkSelfPermsissions()
            Log.e(TAG, "After permissions")

            task.addOnSuccessListener { mLocation ->

                if (mLocation != null) {


                    //Initialize a latitude and longitude

                    //use current locatrion attributes

                    showProgress(pb_sign_add, requireContext())
//                    if (currentLatLng != null) {
//                        val marker = currentLatLng.let {
//                            if (it != null) {
//                                MarkerOptions().position(it)
//                                    .title(resources.getString(R.string.you_are_here))
//                            }
//                        }
//
//                        gMap.moveCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                currentLatLng,
//                                zoomLevel
//                            )
//                        )
//                        gMap.addMarker(marker)
//
//                        fetchedLocation = getAddress(
//                            requireContext(),
//                            pref.getLanguageFromPreference() ?: "en",
//                            currentLatLng
//                        ).getAddressLine(0)
//                        fetchedEnglishAddress =
//                            ExternalUtils.getAddress(requireContext(), "en", currentLatLng)
//                        createSnackbar(fetchedLocation, requireContext(), container_sign_up)
//
//                    }

                    //Use mLocation attributes

                    //Use mLocation
                    val latitudeLongitude =
                        LatLng(mLocation.latitude, mLocation.longitude)
                    val marker = MarkerOptions().position(latitudeLongitude)
                        .title(resources.getString(R.string.you_are_here))

                    //Marker has changed
                    gMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng,
                            zoomLevel
                        )
                    )
                    gMap.addMarker(marker)

                    fetchedLocation = getAddress(
                        requireContext(),
                        pref.getLanguageFromPreference() ?: "en",
                        latitudeLongitude
                    ).getAddressLine(0)
                    fetchedEnglishAddress =
                        ExternalUtils.getAddress(
                            requireContext(),
                            "en",
                            latitudeLongitude
                        )

                    createSnackbar(fetchedLocation, requireContext(), container_sign_up)
                    tvAddress.text = getAddress(
                        requireContext(),
                        pref.getLanguageFromPreference() ?: "en",
                        latitudeLongitude
                    ).getAddressLine(0)



                    hideProgress(pb_sign_add, requireContext())

                    gMap.setOnMapClickListener { newLatLong ->
                        //Create a new marker

                        showProgress(pb_sign_add, requireContext())
                        gMap.clear()
                        val newMarker = MarkerOptions()
                        newMarker.position(newLatLong)
                        newMarker.title(newLatLong.latitude.toString() + "," + newLatLong.longitude)
                        //Remove other markers
                        //Animation and zoom
                        gMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                zoomLevel
                            )
                        )
                        gMap.addMarker(newMarker)

                        //Update the location
                        fetchedLocation = getAddress(
                            requireContext(),
                            pref.getLanguageFromPreference() ?: "en",
                            newLatLong
                        ).getAddressLine(0)

                        fetchedEnglishAddress =
                            ExternalUtils.getAddress(requireContext(), "en", newLatLong)

                        createSnackbar(fetchedLocation, requireContext(), container_sign_up)
                        tvAddress.text = getAddress(
                            requireContext(),
                            pref.getLanguageFromPreference() ?: "en",
                            newLatLong
                        ).getAddressLine(0)
                        hideProgress(pb_sign_add, requireContext())
                    }

                } else {
                    Log.e(TAG, "mLocation is null")
                    errorInCreatingMaps(currentLatLng)
                }
            }


        } else {
            Log.e(TAG, "GMap is null in on map ready")
            errorInCreatingMaps(currentLatLng)
        }


        task.addOnFailureListener { mFailure ->
            Log.e(TAG, "Failure")
            UIUtils.logExceptions(mFailure, TAG)

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "In view created")

        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync { p0 ->
            Log.e(TAG, "In on map main ready")
            MapReady(p0)
        }

    }
}
