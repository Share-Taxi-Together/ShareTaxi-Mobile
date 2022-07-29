package com.example.sharedtaxitogether

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import kotlin.concurrent.thread

class TMapActivity : AppCompatActivity() {

    private val api_key :String= BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView
    lateinit var linearLayoutTmap: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmap)

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.zoomLevel = 14
        //tMapView.setLocationPoint(126.733529, 37.340191)
        //tMapView.setIconVisibility(true)
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        tMapView.setCenterPoint(126.733529, 37.340191)

        //마커 생성
        val markerItem = TMapMarkerItem()
        val tMapPoint = TMapPoint(37.340191,126.733529)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder)

        markerItem.icon = bitmap
        markerItem.setPosition(0.5f, 1.0f)
        markerItem.tMapPoint = tMapPoint
        markerItem.name = "한공대"
        tMapView.addMarkerItem("marker", markerItem)

        //경로 표시
        thread(start=true){
            val tMapPointStart = TMapPoint(37.342016, 126.731758)
            val tMapPointEnd = TMapPoint(37.351422, 126.742382)

            try {
                val tMapPolyLine = TMapData().findPathData(tMapPointStart, tMapPointEnd)
                tMapPolyLine.lineColor = Color.BLACK
                tMapPolyLine.lineWidth = 2.0f
                tMapView.addTMapPolyLine("line", tMapPolyLine)
            } catch(e:Exception) {
                e.printStackTrace();
            }
        }

        //리버스 지오코딩
        try{
            val address = TMapData().convertGpsToAddress(tMapPoint.latitude, tMapPoint.longitude)
            thread {
                Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
            }
        }catch(e:Exception){
            e.printStackTrace()
        }

        linearLayoutTmap = findViewById(R.id.linearLayoutTmap)
        linearLayoutTmap.addView(tMapView)
    }
}