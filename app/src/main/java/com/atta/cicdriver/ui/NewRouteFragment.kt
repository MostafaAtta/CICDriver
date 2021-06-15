package com.atta.cicdriver.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.atta.cicdriver.R
import com.atta.cicdriver.databinding.FragmentNewRouteBinding
import com.atta.cicdriver.model.Point
import com.atta.cicdriver.model.Route
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewRouteFragment : Fragment() {

    private var _binding: FragmentNewRouteBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    var edit: Boolean = false

    var route: Route? = null

    lateinit var points: ArrayList<Point>

    lateinit var morningStartTimes: ArrayList<String>

    lateinit var morningEndTimes: ArrayList<String>

    lateinit var afternoonStartTimes: ArrayList<String>

    lateinit var afternoonEndTimes: ArrayList<String>

    lateinit var morningStartTimesAdapter: ArrayAdapter<String>

    lateinit var morningEndTimesAdapter: ArrayAdapter<String>

    lateinit var afternoonStartTimesAdapter: ArrayAdapter<String>

    lateinit var afternoonEndTimesAdapter: ArrayAdapter<String>

    private lateinit var morningStartTime: String
    private lateinit var morningEndTime: String
    private lateinit var afternoonStartTime: String
    private lateinit var afternoonEndTime: String

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true


        mMap.setOnMapClickListener {
            showNewPointDialog(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        _binding = FragmentNewRouteBinding.inflate(inflater, container, false)
        val view = binding.root
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        db = Firebase.firestore

        edit = arguments?.let { NewRouteFragmentArgs.fromBundle(it).edit }!!


        route = arguments?.let { NewRouteFragmentArgs.fromBundle(it).route }

        initiateViews()

        if (edit){
            showRouteData()
        }


        return view

    }

    private fun showRouteData() {

        binding.routeName.setText(route?.name)
        binding.morningStartSpinner.setSelection(morningStartTimes.indexOf(route?.morningTimes?.get("startTime")))
        binding.morningEndSpinner.setSelection(morningEndTimes.indexOf(route?.morningTimes?.get("endTime")))
        binding.afternoonStartSpinner.setSelection(afternoonStartTimes.indexOf(route?.afternoonTimes?.get("startTime")))
        binding.afternoonEndSpinner.setSelection(afternoonEndTimes.indexOf(route?.afternoonTimes?.get("endTime")))

        binding.busCapacity.setText(route?.busCapacity.toString())

        route?.let { getPoints(it.id) }

    }

    private fun initiateViews(){
        morningStartTimes = resources.getStringArray(R.array.morning_start_times).toList() as ArrayList<String>

        morningEndTimes = resources.getStringArray(R.array.morning_end_times).toList() as ArrayList<String>

        afternoonStartTimes = resources.getStringArray(R.array.afternoon_start_times).toList() as ArrayList<String>

        afternoonEndTimes = resources.getStringArray(R.array.afternoon_end_times).toList() as ArrayList<String>
        setRoutesSpinner()
        if (edit){
            binding.doneBtn.text = getString(R.string.edit_route)
        }else{

            binding.doneBtn.text = getString(R.string.add)
        }


        binding.doneBtn.setOnClickListener {
            if (edit){
                editRoute()
            }else{
                addRoute()
            }
        }
    }

    private fun addRoute() {

        val newRoute = mapOf("driverId" to "",  "driverName" to "",
            "name" to binding.routeName.text.toString(),
            "morningTimes" to mapOf("startTime" to morningStartTime, "endTime" to morningEndTime),
            "afternoonTimes" to mapOf("startTime" to afternoonStartTime, "endTime" to afternoonEndTime),
            "reservedChairs" to 0, "busCapacity" to binding.busCapacity.text.toString().toInt())
        db.collection("Routes")
            .add(newRoute)
            .addOnSuccessListener {
                for (point in points) {
                    addRoutePoint(point, it.id)
                }
                Toast.makeText(context, getString(R.string.done), Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener{
                Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()

            }

    }

    private fun editRoute() {
        val updatedRoute = mapOf("name" to binding.routeName.text.toString(),
            "morningTimes" to mapOf("startTime" to morningStartTime, "endTime" to morningEndTime),
            "afternoonTimes" to mapOf("startTime" to afternoonStartTime, "endTime" to afternoonEndTime),
            "busCapacity" to binding.busCapacity.text.toString().toInt())
        db.collection("Routes")
            .document(route!!.id)
            .update(updatedRoute)
            .addOnSuccessListener {
                Toast.makeText(context, getString(R.string.done), Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener {
                Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()

            }
    }

    private fun showNewPointDialog(latLng: LatLng) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.route_new_point_layout)
        val addBtn = dialog?.findViewById(R.id.addBtn) as Button
        val routePointName = dialog?.findViewById(R.id.pointName) as TextInputEditText

        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        addBtn.setOnClickListener {
            val point = Point(routePointName.text.toString(), GeoPoint(latLng.latitude, latLng.longitude))
            if (edit){
                addRoutePoint(point, route!!.id)
            }else{
                points.add(point)
            }

            dialog.dismiss()
        }


        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show()
    }

    private fun addRoutePoint(point: Point, id: String) {
        val pointMap = mapOf("name" to point.name,
            "location" to point.location)
        db.collection("Routes")
            .document(id)
            .collection("Points")
            .document()
            .set(pointMap)
            .addOnSuccessListener {
                Toast.makeText(context, getString(R.string.point_add), Toast.LENGTH_LONG).show()
                addPointMarker(point)

            }
            .addOnFailureListener{
                Toast.makeText(context, "get failed with  $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getPoints(id: String) {
        db.collection("Routes")
            .document(id)
            .collection("Points")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (document in it){
                        val point = document.toObject(Point::class.java)
                        point.id = document.id
                        addPointMarker(point)
                    }

                }
            }
            .addOnFailureListener{
                Toast.makeText(context, getString(R.string.points_error), Toast.LENGTH_LONG).show()

            }
    }

    private fun addPointMarker(point: Point) {
        val latLng = LatLng(point.location.latitude, point.location.longitude)

        var marker = MarkerOptions().position(latLng)
            .title(point.name)

        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
    }

    private fun setRoutesSpinner(){
        morningStartTimesAdapter = context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_list_item_1,
                morningStartTimes
            )
        }!!

        binding.morningStartSpinner.adapter = morningStartTimesAdapter

        binding.morningStartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                morningStartTime = morningStartTimes[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
        morningEndTimesAdapter = context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_list_item_1,
                morningEndTimes
            )
        }!!

        binding.morningEndSpinner.adapter = morningEndTimesAdapter

        binding.morningEndSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                morningEndTime = morningEndTimes[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }

        afternoonStartTimesAdapter = context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_list_item_1,
                afternoonStartTimes
            )
        }!!

        binding.afternoonStartSpinner.adapter = afternoonStartTimesAdapter

        binding.afternoonStartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                afternoonStartTime = afternoonStartTimes[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }

        afternoonEndTimesAdapter = context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_list_item_1,
                afternoonEndTimes
            )
        }!!

        binding.afternoonEndSpinner.adapter = afternoonEndTimesAdapter

        binding.afternoonEndSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                afternoonEndTime = afternoonEndTimes[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}