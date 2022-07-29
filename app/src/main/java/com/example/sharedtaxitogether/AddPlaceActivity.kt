package com.example.sharedtaxitogether

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapView
import java.util.jar.Manifest

class AddPlaceActivity:AppCompatActivity() {

    private val api_key :String= BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView
    lateinit var linearLayoutMap: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.zoomLevel = 14
        tMapView.setIconVisibility(true)
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        //tMapView.setCenterPoint(126.733529, 37.340191)

        linearLayoutMap = findViewById(R.id.linearLayoutMap)
        linearLayoutMap.addView(tMapView)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, 100)
        } else{
            val tMapGPS = TMapGpsManager(this)
            tMapGPS.minTime = 1000
            tMapGPS.minDistance = 10F
            tMapGPS.provider = TMapGpsManager.GPS_PROVIDER

            tMapGPS.OpenGps()
        }
    }
}