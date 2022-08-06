package com.example.sharedtaxitogether

import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityAddPlaceBinding
import com.skt.Tmap.*

class AddPlaceActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {
    private lateinit var binding: ActivityAddPlaceBinding
    private val api_key: String = BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView
    lateinit var tMapPoint: TMapPoint
    lateinit var tMapData: TMapData
    lateinit var tMapGPS: TMapGpsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.setIconVisibility(true)
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
//        //나침반모드
//        tMapView.setCompassMode(true)
//        tMapView.setSightVisible(true)

        binding.linearLayoutMap.addView(tMapView)

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestPermissions(permissions, 100)
        }

        tMapGPS = TMapGpsManager(this)

        tMapGPS.minTime = 1000
        tMapGPS.minDistance = 10F
        tMapGPS.provider = TMapGpsManager.NETWORK_PROVIDER

        tMapGPS.OpenGps()

        //추가버튼 누르면 현재 위치의 주소, 위도, 경도값 가져오기
        bind()
    }

    private fun bind() {
        binding.cancelBtn.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            tMapPoint = tMapView.getTMapPointFromScreenPoint(100F, 100F)
            val latitude = tMapPoint.latitude
            val longitude = tMapPoint.longitude

            binding.latitudeText.text = latitude.toString()
            binding.longitudeText.text = longitude.toString()

            //위도경도로 주소반환
            tMapData = TMapData()
            Thread {
                val address = tMapData.convertGpsToAddress(latitude, longitude)
                binding.addressText.text = address
            }.start()
        }
    }

    override fun onLocationChange(location: Location) {
        tMapView.setLocationPoint(location.longitude, location.latitude)
        tMapView.setCenterPoint(location.longitude, location.latitude)
    }
}