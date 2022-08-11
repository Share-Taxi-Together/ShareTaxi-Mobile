package com.example.sharedtaxitogether

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityShowpathBinding
import com.example.sharedtaxitogether.model.Share
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import kotlin.concurrent.thread

class ShowPathActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowpathBinding

    private val api_key: String = BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowpathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getSerializableExtra("place") as Share

        initData(data)
        bind()
    }

    private fun initData(data: Share) {
        binding.startPlace.text = data.place["start"]!!.id
        binding.destPlace.text = data.place["dest"]!!.id

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.zoomLevel = 14
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setCenterPoint(126.733529, 37.340191)

        //경로 표시
        thread(start = true) {
            val tMapPointStart = TMapPoint(
                data.place["start"]!!.latitude.toDouble(),
                data.place["start"]!!.longitude.toDouble()
            )
            val tMapPointEnd = TMapPoint(
                data.place["dest"]!!.latitude.toDouble(),
                data.place["dest"]!!.longitude.toDouble()
            )

            try {
                val tMapPolyLine = TMapData().findPathData(tMapPointStart, tMapPointEnd)
                tMapPolyLine.lineColor = Color.BLUE
                tMapPolyLine.lineWidth = 5.0f
                tMapView.addTMapPath(tMapPolyLine)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.showPathActivityLinear.addView(tMapView)
    }

    private fun bind() {
        binding.showPathBackBtn.setOnClickListener {
            finish()
        }
    }
}
