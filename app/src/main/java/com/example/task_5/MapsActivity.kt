package com.example.task_5

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.task_5.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import io.reactivex.Observable.fromIterable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.math.pow

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val homiel = LatLng(52.42416, 31.014281)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homiel, 15f))
        getMarkers()
    }

    private fun getMarkers() {
        CoroutineScope(Dispatchers.Default).launch {
            while (!checkInternetConnection()) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.check_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                delay(6000)
            }
            Single.zip(
                BankApiImpl.getListOfBanks(),
                BankApiImpl.getListOfFilials(),
                BankApiImpl.getListOfInfobox(),
                { banks, filials, infoboxes ->
                    banks.forEach { it.type = getString(R.string.atm) }
                    filials.forEach { it.type = getString(R.string.filial) }
                    infoboxes.forEach { it.type = getString(R.string.infobox) }
                    banks + filials + infoboxes
                })
                .map { list ->
                    list.sortedWith(
                        compareBy {
                            kotlin.math.sqrt(
                                (gps_x - it.gps_x).pow(2) + (gps_y - it.gps_y).pow(
                                    2
                                )
                            )
                        }
                    )
                }
                .flatMapObservable { items -> fromIterable(items) }
                .take(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { value -> addMarkers(value) }
        }
    }

    private fun addMarkers(value: Bank) {
        mMap.addMarker(
            MarkerOptions().position(LatLng(value.gps_x, value.gps_y))
                .title(value.type)
                .snippet("${value.address_type.plus(" ") ?: ""}${value.address.plus(" ") ?: ""}${value.house ?: ""}")
                .icon(
                    when (value.type) {
                        getString(R.string.atm) -> BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_BLUE
                        )
                        getString(R.string.filial) -> BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                        getString(R.string.infobox) -> BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE
                        )
                        else -> {
                            null
                        }
                    }
                )
        )
    }

    private fun checkInternetConnection(): Boolean {
        val connection =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connection.activeNetwork != null
    }

    companion object {
        const val gps_x = 52.425163
        const val gps_y = 31.015039
    }
}
