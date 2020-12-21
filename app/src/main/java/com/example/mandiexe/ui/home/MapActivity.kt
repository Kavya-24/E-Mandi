package com.example.mandiexe.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mandiexe.R
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MapActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = MapActivity()
    }

    private var supportMapFragment = SupportMapFragment()
    private lateinit var client: FusedLocationProviderClient
    private lateinit var root: View
    private val permissionRequestCode = 1234
    private val RESULT_OK = 111
    private val TAG = MapActivity::class.java.simpleName
    private lateinit var fab: FloatingActionButton
    private var fetchedLocation: String = ""

    private val pref = PreferenceUtil
    private var args: Bundle? = null

    private var fromSignUp = false

    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.map_fragment)

        setAppLocale(pref.getLanguageFromPreference().toString(), this)

        args = intent?.getBundleExtra("bundle")

        if (args != null) {
            //Then this is from signUp
            fromSignUp = true
        }

        fab = findViewById(R.id.fav_check_map)


        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        client = this.let { LocationServices.getFusedLocationProviderClient(it) }!!

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
        if (fetchedLocation == "" || fetchedLocation == "Not found") {
            tvAddress.text = args?.getString("ADDRESS_USER")
        } else {
            tvAddress.text = fetchedLocation
        }


        //Positive and negative buttons

        //Create observer on Text

        d.setPositiveButton("Register") { _, _ ->

            createUser()


        }
        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        tempRef = d.create()
        d.show()


    }

    private fun createUser() {
        tempRef.dismiss()
        //Create a new user call

        /**
         * Test
         */
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)

    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this as Activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
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
            //Go to it

            getLocationInMap()
        }

        if (p != PackageManager.PERMISSION_GRANTED) {
            //Not permitted
            Toast.makeText(this, "Permissions needed", Toast.LENGTH_SHORT).show()


        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //Tell user what we are going to do with this permission
            val b = AlertDialog.Builder(this)
            b.setMessage("Permission to access location")
            b.setTitle("Permission required")
            b.setPositiveButton("Ok") { dialog: DialogInterface?, which: Int ->
                makeRequest()
            }
            val dialog = b.create()
            dialog.show()
        } else {
            makeRequest()
        }

    }

    private fun getLocationInMap() {

        val task: Task<Location> = client.lastLocation

        task.addOnSuccessListener { mLocation ->

            if (mLocation != null) {
                supportMapFragment.getMapAsync(object : OnMapReadyCallback {

                    override fun onMapReady(gMap: GoogleMap?) {
                        //Initialize a latitude and longitude
                        val latitudeLongitude = LatLng(mLocation.latitude, mLocation.longitude)
                        val marker = MarkerOptions().position(latitudeLongitude)
                            .title(resources.getString(R.string.you_are_here))


                        if (gMap != null) {
                            gMap.animateCamera(CameraUpdateFactory.newLatLng(latitudeLongitude))
                            gMap.addMarker(marker)

                            //Mark this as the current location
                            fetchedLocation = getAddress(latitudeLongitude)

                        } else {
                            Toast.makeText(
                                this@MapActivity,
                                "Unable to open maps",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        gMap?.setOnMapClickListener { newLatLong ->
                            //Create a new marker

                            gMap.clear()
                            val newMarker = MarkerOptions()
                            newMarker.position(newLatLong)
                            newMarker.title(newLatLong.latitude.toString() + "," + newLatLong.longitude)
                            //Remove other markers
                            //Animation and zoom
                            gMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLong))
                            gMap.addMarker(newMarker)

                            //Update the location
                            fetchedLocation = getAddress(newLatLong)


                        }


                    }

                })
            } else {
                fetchedLocation = "Not found"
            }
        }

    }

    private fun getAddress(latLang: LatLng): String {
        var theAddress = ""

        //##Get location
        val locale = Locale(pref.getLanguageFromPreference().toString())
        Log.e(TAG, " fjf " + pref.getLanguageFromPreference() + " ")

        val geocoder = Geocoder(this, locale)
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLang.latitude, latLang.longitude, 1)
            val mAddress = addresses.get(0).getAddressLine(0)
            Log.e(TAG, mAddress.toString())
            theAddress = mAddress

        } catch (e: Exception) {
            return e.message.toString()
        }

        return theAddress
    }

    override fun onBackPressed() {

        val bundle = Bundle()

        bundle.putString("fetchedLocation", fetchedLocation)
        Log.e(TAG, "In back pressed and address is " + fetchedLocation.toString())
        val mIntent = Intent()
        mIntent.putExtras(bundle)
        setResult(RESULT_OK, mIntent)
        super.onBackPressed()
    }

}
