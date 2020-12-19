package com.example.mandiexe.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mandiexe.R
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

class MapActivity : Fragment() {

    companion object {
        fun newInstance() = MapActivity()
    }

    private var supportMapFragment = SupportMapFragment()
    private lateinit var client: FusedLocationProviderClient
    private lateinit var root: View
    private val permissionRequestCode = 1234
    private val TAG = MapActivity::class.java.simpleName
    private lateinit var fab: FloatingActionButton
    private lateinit var fetchedLocation: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.map_fragment, container, false)
        fab = root.findViewById(R.id.fav_check_map)

        Log.e(TAG, "In on create and suppor" + supportMapFragment.toString())

        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        client = context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        getPermissions()

        fab.setOnClickListener {

            val bundle = bundleOf("fetchedLocation" to fetchedLocation)
            root.findNavController().navigate(R.id.action_mapActivity_to_addStock, bundle)
        }
        return root
    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            context as Activity,
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
                    Toast.makeText(context, "Permission denied by user", Toast.LENGTH_SHORT).show()

                } else {
                    getLocationInMap()

                }

        }


    }

    private fun getPermissions() {

        Log.e(TAG, "In get permissions")
        val p = this.let {
            context?.let { it1 ->
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
            Toast.makeText(context, "Permissions needed", Toast.LENGTH_SHORT).show()


        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //Tell user what we are going to do with this permission
            val b = AlertDialog.Builder(context)
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
                                context,
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
            }
        }

    }

    private fun getAddress(latLang: LatLng): String {
        var theAddress = ""

        //##Get location
        val geocoder = Geocoder(context, Locale.getDefault())
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

}
