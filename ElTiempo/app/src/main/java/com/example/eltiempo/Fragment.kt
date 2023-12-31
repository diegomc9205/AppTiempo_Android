package com.example.eltiempo

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.eltiempo.databinding.FragmentBinding
import com.example.eltiempo.ui.MyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class Fragment : Fragment() {

    private var _binding: FragmentBinding? = null
    private lateinit var locationClient: FusedLocationProviderClient


    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun mostrarUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationClient.lastLocation.addOnSuccessListener {
            viewModel.tiempo(it.latitude,it.longitude)

        }

    }

    private fun pedirPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            mostrarUbicacion()

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showAlertDialog()
        } else {
            somePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Los permisos de ubicación permitirán ver tu ubicación en el mapa. Los creadores no almacenan esta información en ningún lugar")
        builder.setNegativeButton("Rechazar", null)
        builder.setPositiveButton("Aceptar") { _, _ ->
            somePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        builder.create().show()
    }

    private val somePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (!it.containsValue(false)) {
            mostrarUbicacion()
        } else {

            Toast.makeText(requireContext(), "No se han aceptado los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        pedirPermisosUbicacion()

        viewModel.liveData.observe(viewLifecycleOwner){

            binding.ciudad.text = it?.name
            binding.grados.text = it?.main?.temp.toString() + "º"
            binding.tempMax.text = "↑" + it?.main?.tempMax.toString()
            binding.tempMin.text = "↓" + it?.main?.tempMin.toString()
            binding.TextNube.text = it?.clouds?.all.toString()+"%"
            binding.resultSencacionTermica.text = it?.main?.feelsLike.toString()+"º"
            binding.resultPresion.text = it?.main?.pressure.toString() +" hPa"
            binding.resultHumedad.text = it?.main?.seaLevel.toString() + " hPa"
            binding.resultHumedad.text = it?.main?.humidity.toString() + " %"
            binding.resultNivelDelMar.text = it?.main?.grndLevel.toString() + " hPa"
            binding.resultNivelTierra.text = it?.wind?.speed.toString() + " m/s " + it?.wind?.deg.toString() + "º"
            binding.resultViento.text = it?.visibility.toString() + " km"

            val timeStamp = it?.sys?.sunrise?.times(1000)
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = timeStamp?.let { it1 -> Date(it1.toLong()) }?.let { it2 -> sdf.format(it2) }

            binding.TextAtardecer.text = time

            val timeStamp2 = it?.sys?.sunset?.times(1000)
            val sdf2 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time2 = timeStamp2?.let { it1 -> Date(it1.toLong()) }?.let { it2 -> sdf2.format(it2) }

            binding.TextAmanecer.text = time2




        }




    }








}