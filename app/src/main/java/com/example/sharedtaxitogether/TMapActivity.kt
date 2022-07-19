package com.example.sharedtaxitogether

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.skt.Tmap.TMapView

class TMapActivity : AppCompatActivity(){

    private val api_key :String= BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView
    lateinit var linearLayoutTmap: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmap)

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.zoomLevel = 15
        tMapView.setLocationPoint(126.733529, 37.340191)
        tMapView.setIconVisibility(true)
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        tMapView.setCenterPoint(126.733529, 37.340191)

        linearLayoutTmap = findViewById(R.id.linearLayoutTmap)
        linearLayoutTmap.addView(tMapView)
    }
}