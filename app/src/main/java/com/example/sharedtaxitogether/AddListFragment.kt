package com.example.sharedtaxitogether

import android.R
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sharedtaxitogether.databinding.FragmentAddBinding
import com.example.sharedtaxitogether.model.Place
import com.example.sharedtaxitogether.viewModel.addInfoViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AddListFragment : Fragment() {
    private val api_key: String = BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView

    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentAddBinding
    private val db = FirebaseFirestore.getInstance()
    private val viewModel: addInfoViewModel by viewModels()

    private val placeList = arrayListOf<Place>()
    private val nameList = arrayListOf<String>()

    lateinit var start: String
    lateinit var dest: String

    // TODO LiveData 합승정보 추가 및 observer
    // todo 출발, 도착 장소 다른지 체크

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        Log.d("here onAttach", "onAttach")

        //db에서 장소 정보 가져와 저장 Place(id, address, longitude, latitude)
        getPlace()

        //합승인원구할건지 여부 확인
        checkAdd()

        binding = FragmentAddBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.startLatitute.observe(this){

        }

        tMapView = TMapView(mainActivity)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.zoomLevel = 14
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setCenterPoint(126.733529, 37.340191)

        //마커 생성
        val markerItem = TMapMarkerItem()
        val tMapPoint = TMapPoint(37.340191, 126.733529)
        val bitmap = BitmapFactory.decodeResource(
            resources,
            com.example.sharedtaxitogether.R.drawable.placeholder
        )

        markerItem.icon = bitmap
        markerItem.setPosition(0.5f, 1.0f)
        markerItem.tMapPoint = tMapPoint
        markerItem.name = "한공대"
        tMapView.addMarkerItem("marker", markerItem)

        binding.layoutTmap.addView(tMapView)


        binding.showRoute.setOnClickListener {
            if (start.isNotBlank() && dest.isNotBlank()) {
                //경로 표시
                thread(start = true) {
                    val tMapPointStart = TMapPoint(
                        viewModel.startLatitute.value!!.toDouble(),
                        viewModel.startLongitude.value!!.toDouble()
                    )
                    val tMapPointEnd = TMapPoint(
                        viewModel.destLatitude.value!!.toDouble(),
                        viewModel.destLongitude.value!!.toDouble()
                    )
                    try {
                        val tMapPolyLine = TMapData().findPathData(tMapPointStart, tMapPointEnd)
                        tMapPolyLine.lineColor = Color.BLACK
                        tMapPolyLine.lineWidth = 2.0f
                        tMapView.addTMapPolyLine("line", tMapPolyLine)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.addPlace.setOnClickListener {
            //custom dialog 만들어서 장소 추가하기
            // 지도띄우기, 핀 움직여서 원하는 장소의 주소 받아오기
            // 이름작성, 주소는 위에서 받아오고
            // 추가하기 버튼 누르면 db에 추가

            startActivity(Intent(mainActivity, AddPlaceActivity::class.java))
            //count는 넣을지말지 고민중,,,
            // 넣는다면 선택될때마다 세서 장소목록 보여줄 때 count 많은 순으로 정렬하기
        }

        binding.spinnerNum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // TODO num 값 활용하기
                var num = binding.spinnerNum.getItemAtPosition(position).toString()
            }
        }

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // TODO gender 값 활용하기
                val gender = binding.spinnerGender.getItemAtPosition(position).toString()
                Log.d("here gender", gender)
            }
        }

        binding.timepicker.setOnClickListener {
            getTime()
        }

        return binding.root
    }

    private fun getPlace() {
        db.collection("places")
            .get()
            .addOnSuccessListener { result ->
                nameList.clear()
                for (document in result) {
                    val item = Place(
                        document["id"] as String,
                        document["address"] as String,
                        document["latitude"] as String,
                        document["longitude"] as String
                    )
                    nameList.add(document["id"] as String)
                    placeList.add(item)
                }
                placeList.add(Place("선택", "", "", ""))
                nameList.add("--선택--")
            }
    }

    private fun checkAdd() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setMessage("합승인원을 구하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                setSpinner()
            }
            .setNegativeButton("아니요") { _, _ ->
                startActivity(Intent(mainActivity, MainActivity::class.java))
            }
        builder.show()
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter(mainActivity, R.layout.simple_list_item_1, nameList)

        binding.spinnerStart.adapter = adapter
        binding.spinnerDest.adapter = adapter

        binding.spinnerStart.setSelection(nameList.size - 1)
        binding.spinnerDest.setSelection(nameList.size - 1)

        binding.spinnerStart.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    start = binding.spinnerStart.getItemAtPosition(position).toString()
                    viewModel.startLongitude.value = placeList[position].longitude
                    viewModel.startLatitute.value = placeList[position].latitude
                    binding.textStart.text = placeList[position].address

                    Log.d("here", viewModel.startLongitude.value!!)
                }
            }

        binding.spinnerDest.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    dest = binding.spinnerDest.getItemAtPosition(position).toString()
                    viewModel.destLatitude.value = placeList[position].latitude
                    viewModel.destLongitude.value = placeList[position].longitude
                    binding.textDest.text = placeList[position].address
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime() {
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            binding.timeText.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        TimePickerDialog(
            mainActivity, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }
}