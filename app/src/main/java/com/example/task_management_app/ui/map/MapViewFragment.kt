package com.example.task_management_app.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.task_management_app.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapViewFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var map: GoogleMap
    private var currentMarker: Marker? = null
    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate o layout do fragmento
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Busque o fragmento do mapa pelo childFragmentManager
        val mapFragment = childFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configure o botão de remover marcador
        val removeMarkerButton: Button? = view.findViewById(R.id.remove_marker_button)
        removeMarkerButton?.setOnClickListener {
            removeMarker()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Define o listener para cliques no mapa
        map.setOnMapClickListener(this)

        // Movendo a câmera para um local específico (exemplo)
        val initialLocation = LatLng(-23.5505, -46.6333) // São Paulo, Brasil
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
    }

    override fun onMapClick(latLng: LatLng) {
        // Remover o marcador atual, se existir
        currentMarker?.remove()

        // Adicionar um novo marcador no local clicado
        val markerTitle = getLocationName(latLng) ?: "Local Selecionado"
        val markerOptions = MarkerOptions().position(latLng).title(markerTitle)
        currentMarker = map.addMarker(markerOptions)

        // Movendo a câmera para o local clicado
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        // Salvar a localização selecionada
        selectedLocation = latLng
    }

    private fun removeMarker() {
        // Remover o marcador atual, se existir
        currentMarker?.remove()
        currentMarker = null
        selectedLocation = null
        Toast.makeText(context, "Marcador removido", Toast.LENGTH_SHORT).show()
    }

    private fun getLocationName(latLng: LatLng): String? {
        // Verifique se o contexto não é nulo
        val context = context ?: return null
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) // Retorna o endereço completo
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Função para recuperar a localização selecionada
    fun getSelectedLocation(): LatLng? {
        return selectedLocation
    }
}
