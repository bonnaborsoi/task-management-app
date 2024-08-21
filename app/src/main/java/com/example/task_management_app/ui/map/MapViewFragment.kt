package com.example.task_management_app.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.task_management_app.R
import com.example.task_management_app.data.model.Task
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
        // Inflate o layout do fragmento
        return inflater.inflate(R.layout.fragment_map, container, false)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getString(ARG_TASK_ID)
            taskLocation = it.getString(ARG_TASK_LOCATION)
            // Use the taskId to retrieve the Task from your data source
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*val removeMarkerButton: Button? = view.findViewById(R.id.remove_marker_button)
        removeMarkerButton?.setOnClickListener {
            removeMarker()
        }*/
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
                    }
                } else {
                    Log.e("MapViewFragment", "Address not found: $it")
                }
            } catch (e: Exception) {
                Log.e("MapViewFragment", "Geocoding error", e)
            }
        }
    }

//        // Define o listener para cliques no mapa
//        map.setOnMapClickListener(this)
//
//        // Movendo a câmera para um local específico (exemplo)
//        val initialLocation = LatLng(-23.5505, -46.6333) // São Paulo, Brasil
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
//    }
//
//    override fun onMapClick(latLng: LatLng) {
//        Log.d("onMap","$taskId")
//        // Remover o marcador atual, se existir
//        currentMarker?.remove()
//
//        // Adicionar um novo marcador no local clicado
//        val markerTitle = getLocationName(latLng) ?: "Local Selecionado"
//        val markerOptions = MarkerOptions().position(latLng).title(markerTitle)
//        currentMarker = map.addMarker(markerOptions)
//
//        // Movendo a câmera para o local clicado
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//
//        // Salvar a localização selecionada
//        selectedLocation = latLng
//    }
//
//    private fun removeMarker() {
//        // Remover o marcador atual, se existir
//        currentMarker?.remove()
//        currentMarker = null
//        selectedLocation = null
//        Toast.makeText(context, "Marcador removido", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun getLocationName(latLng: LatLng): String? {
//        // Verifique se o contexto não é nulo
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
//    // Função para recuperar a localização selecionada
//    fun getSelectedLocation(): LatLng? {
//        return selectedLocation
//    }
}
