package com.example.sharedtaxitogether

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityAddPlaceBinding
import com.example.sharedtaxitogether.model.Place
import com.example.sharedtaxitogether.viewModel.AddPlaceViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.skt.Tmap.*

class AddPlaceActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {
    private lateinit var binding: ActivityAddPlaceBinding
    private val api_key: String = BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView

    private lateinit var tMapPoint: TMapPoint
    private lateinit var tMapData: TMapData
    lateinit var tMapGPS: TMapGpsManager

    private val viewModel: AddPlaceViewModel by viewModels()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.mapType = TMapView.MAPTYPE_STANDARD

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
        binding.selectPlace.setOnClickListener {
            //화면 좌표
            tMapPoint = tMapView.getTMapPointFromScreenPoint(
                viewModel.pointX.value!!.toFloat(),
                viewModel.pointY.value!!.toFloat()
            )
            viewModel.latitude.value = tMapPoint.latitude
            viewModel.longitude.value = tMapPoint.longitude

            Log.d("here latitude", viewModel.latitude.value.toString())
            Log.d("here longitude", viewModel.longitude.value.toString())

//            binding.latitudeText.text = viewModel.latitude.value.toString()
//            binding.longitudeText.text = viewModel.longitude.value.toString()

            //위도경도로 주소반환
            tMapData = TMapData()
            Thread {
                viewModel.address.postValue(tMapData.convertGpsToAddress(
                    viewModel.latitude.value!!,
                    viewModel.longitude.value!!
                ))
                binding.addressText.text = viewModel.address.value
            }.start()
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            viewModel.id.value = binding.placeName.text.toString()
            val place =
                Place(
                    viewModel.id.value!!,
                    viewModel.address.value!!,
                    viewModel.latitude.value.toString(),
                    viewModel.longitude.value.toString()
                )

            Log.d("here add", place.id)
            Log.d("here add", place.address)
            Log.d("here add", place.latitude)
            Log.d("here add", place.longitude)

//            db.collection("places").document(place.id).set(place)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "장소가 추가되었습니다", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "장소추가에 실패했습니다", Toast.LENGTH_SHORT).show()
//                    Log.w("here", "Error adding document", e)
//                }
        }
    }

    override fun onLocationChange(location: Location) {
        tMapView.setLocationPoint(location.longitude, location.latitude)
        tMapView.setCenterPoint(location.longitude, location.latitude)

        //화면 좌표 가져오기
        val location = IntArray(2) { 0 }
        binding.marker.getLocationOnScreen(location)
        viewModel.pointX.value = location[0] //x좌표
        viewModel.pointY.value = location[1] //y좌표
    }
}