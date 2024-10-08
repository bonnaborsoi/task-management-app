package com.example.task_management_app.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.task_management_app.R
import com.example.task_management_app.ui.tasklist.TaskListFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapViewFragment : Fragment(), OnMapReadyCallback/*, GoogleMap.OnMapClickListener*/ {

    private lateinit var map: GoogleMap
    private var currentMarker: Marker? = null
    private var selectedLocation: LatLng? = null
    /*private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            task = it.getParcelable("task") ?: throw IllegalArgumentException("Task must be provided")
        }
    }*/

    companion object {
        private const val ARG_TASK_ID = "task_id"
        private const val ARG_TASK_LOCATION = "task_location"

        fun newInstance(taskId: String, taskLocation: String): MapViewFragment {
            val fragment = MapViewFragment()
            val args = Bundle().apply {
                putString(ARG_TASK_ID, taskId)
                putString(ARG_TASK_LOCATION, taskLocation)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var taskId: String? = null
    private var taskLocation: String? = null

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getString(ARG_TASK_ID)
            taskLocation = it.getString(ARG_TASK_LOCATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val backToTaskListButton: Button? = view.findViewById(R.id.backToTaskList)
        backToTaskListButton?.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, TaskListFragment())
                addToBackStack(null)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        taskLocation?.let {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val addressList = geocoder.getFromLocationName(it, 1)
                if (addressList != null) {
                    if (addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(it))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    } else {
                        Log.e("MapViewFragment", "Address not found: $it")
                        Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.commit {
                            replace(R.id.fragment_container, TaskListFragment())
                            addToBackStack(null)
                        }
                    }
                } else {
                    Log.e("MapViewFragment", "Address not found: $it")
                    Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.commit {
                        replace(R.id.fragment_container, TaskListFragment())
                        addToBackStack(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("MapViewFragment", "Geocoding error", e)
                Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show()
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, TaskListFragment())
                    addToBackStack(null)
                }
            }
        }
    }

//        map.setOnMapClickListener(this)

//        val initialLocation = LatLng(-23.5505, -46.6333) // São Paulo, Brasil
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
//    }
//
//    override fun onMapClick(latLng: LatLng) {
//        Log.d("onMap","$taskId")
//        currentMarker?.remove()

//        val markerTitle = getLocationName(latLng) ?: "Local Selecionado"
//        val markerOptions = MarkerOptions().position(latLng).title(markerTitle)
//        currentMarker = map.addMarker(markerOptions)
//
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//        selectedLocation = latLng
//    }
//
//    private fun removeMarker() {
//        currentMarker?.remove()
//        currentMarker = null
//        selectedLocation = null
//        Toast.makeText(context, "Marcador removido", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun getLocationName(latLng: LatLng): String? {
//        val context = context ?: return null
//        val geocoder = Geocoder(context, Locale.getDefault())
//
//        return try {
//            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//            if (!addresses.isNullOrEmpty()) {
//                addresses[0].getAddressLine(0) // Retorna o endereço completo
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    fun getSelectedLocation(): LatLng? {
//        return selectedLocation
//    }
}
