package com.atta.cicdriver.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.atta.cicdriver.LocationReceiver
import com.atta.cicdriver.R
import com.atta.cicdriver.model.Route
import com.atta.cicdriver.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {


    private lateinit var mMap: GoogleMap

    lateinit var mLocation: Location

    lateinit var locationRequest: LocationRequest

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var db: FirebaseFirestore


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        if (context?.let { checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED
                && context?.let { checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {

            requestPermission()
            return@OnMapReadyCallback
        }

        if (context?.let { SessionManager.with(it).getUserType() } == "1") {
            updateLocation()
        }else if (context?.let { SessionManager.with(it).getUserType() } == "0") {
            getRouts()
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_home, container, false)

        instance = this

        db = Firebase.firestore
        //getRouteName()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permission, FINE_LOCATION_REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (permissions[0] === Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true)
                //fetchLocation()
            }
        }
    }
    private fun fetchLocation() {

        val result = context?.let { checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }

        if (result != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            mLocation = location
            val myLatLng = LatLng(mLocation.latitude, mLocation.longitude)

            val myLatLng2 = LatLng(30.058205443102477, 31.345448798902545)
            var marker = MarkerOptions().position(myLatLng).title("Softagi")
            mMap.clear()
            mMap.addMarker(marker)
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16f))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
        }
    }
    fun updateLocationOnMap(location: Location) {

        mLocation = location
        val myLatLng = LatLng(mLocation.latitude, mLocation.longitude)
        val icon: BitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.school_bus_marker)

        val routeName = context?.let { SessionManager.with(it).getRouteName() }

        var marker = MarkerOptions().position(myLatLng)
                .title(routeName)
                .icon(icon)
        mMap.clear()
        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14f))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
/*
        val icon2: BitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.passenger)
        val myLatLng2 = LatLng(30.058205443102477, 31.345448798902545)
        var marker2 = MarkerOptions().position(myLatLng2)
                .title(routeName)
                .icon(icon2)
        mMap.addMarker(marker2)*/
        if (context?.let { SessionManager.with(it).getUserType() } == "1") {
            setLocation(location)
        }
    }

    private fun updateLocation(){
        buildLocationRequest()
        val result = context?.let { checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }

        if (result != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
            return
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())

    }

    private fun getPendingIntent(): PendingIntent {

        val intent = Intent(context, LocationReceiver::class.java)
        intent.action = LocationReceiver.ACTION_LOCATION_UPDATE
        return  PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    private fun buildLocationRequest(){
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setSmallestDisplacement(10f)
    }

    fun getRouteName(){
        db.collection("Routes")
                .whereEqualTo("driverId", context?.let { SessionManager.with(it).getUserId() })
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty){
                        for (document in it){
                            val route = document.toObject(Route::class.java)
                            //routeName = route.name
                        }
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "get failed with ", it)
                    Toast.makeText(context, "get failed with  $it", Toast.LENGTH_SHORT).show()
                }
    }

    fun setLocation(location: Location){

        val geoPoint = GeoPoint(location.latitude, location.longitude)
        val newLocation = mapOf("driverLocation" to geoPoint)
        val routeId = context?.let { SessionManager.with(it).getRouteId() }
        db.collection("Routes")
                .document(routeId!!)
                .set(newLocation, SetOptions.merge())
                .addOnSuccessListener {

                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {

                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

    }

    companion object {
        private const val FINE_LOCATION_REQUEST_CODE = 101

        private const val TAG = "HomeFragment"

        var instance: HomeFragment? = null

        fun getHomeInstance(): HomeFragment?{
            return instance
        }
    }

    private fun getRouts() {
        db.collection("Routes")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){
                            val route = document.toObject(Route::class.java)
                            if (route.driverLocation.latitude != 0.0) {
                                addBusMarker(route)
                            }

                            //addRoute(route)
                        }
                        //checklists = it.toObjects<Checklist>()

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

    }

    private fun addBusMarker(route: Route){

        val latLng = LatLng(route.driverLocation.latitude, route.driverLocation.longitude)
        val icon: BitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.school_bus_marker)

        val routeName = context?.let { SessionManager.with(it).getRouteName() }

        var marker = MarkerOptions().position(latLng)
                .title(route.name)
                .icon(icon)
        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

    }


}