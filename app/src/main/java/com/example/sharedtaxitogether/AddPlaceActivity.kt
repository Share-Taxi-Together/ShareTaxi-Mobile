package com.example.sharedtaxitogether

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sharedtaxitogether.databinding.ActivityAddPlaceBinding
import com.example.sharedtaxitogether.dialog.LoadingDialog
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

        val dialog = LoadingDialog(this)
        dialog.show()

        db = FirebaseFirestore.getInstance()

        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.mapType = TMapView.MAPTYPE_STANDARD

        binding.linearLayoutMap.addView(tMapView)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, 100)
        } else {
            tMapGPS = TMapGpsManager(this)

            tMapGPS.minTime = 1000
            tMapGPS.minDistance = 10F
            tMapGPS.provider = TMapGpsManager.NETWORK_PROVIDER

            tMapGPS.OpenGps()
        }

        bind()
        dialog.dismiss()
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
                viewModel.address.postValue(
                    tMapData.convertGpsToAddress(
                        viewModel.latitude.value!!,
                        viewModel.longitude.value!!
                    )
                )
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

            db.collection("places").document(place.id).set(place)
                .addOnSuccessListener {
                    Toast.makeText(this, "장소가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "장소추가에 실패했습니다", Toast.LENGTH_SHORT).show()
                    Log.w("here", "Error adding document", e)
                }
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