package com.example.sharedtaxitogether

import android.R
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.*
import com.example.sharedtaxitogether.databinding.FragmentAddBinding
import com.example.sharedtaxitogether.model.Place
import com.example.sharedtaxitogether.model.Share
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.example.sharedtaxitogether.viewModel.addInfoViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AddListFragment : Fragment() {
    private val listFragment = ListFragment()

    private val api_key: String = BuildConfig.TMAP_API_KEY
    lateinit var tMapView: TMapView

    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentAddBinding
    private val db = FirebaseFirestore.getInstance()
    private val viewModel: addInfoViewModel by viewModels()
    private val userViewModel: LoginViewModel by activityViewModels()

    private val placeList = arrayListOf<Place>()
    private val nameList = arrayListOf<String>()

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
        viewModel.startLatitude.observe(this) {

        }

        tMapView = TMapView(mainActivity)
        tMapView.setSKTMapApiKey(api_key)

        tMapView.zoomLevel = 14
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setCenterPoint(126.733529, 37.340191)

        binding.layoutTmap.addView(tMapView)

        binding.addListBtn.setOnClickListener {
            //todo 모든 항목 선택완료했는지 확인하기
            val share = Share(
                userViewModel.uid.value!!,
                userViewModel.imgUrl.value!!,
                userViewModel.nickname.value!!,
                userViewModel.gender.value!!,
                1,
                viewModel.start.value!!,
                viewModel.dest.value!!,
                viewModel.memberNum.value!!,
                viewModel.memberGender.value!!,
                viewModel.time.value!!
            )
            db.collection("shares").document()
                .set(share)
                .addOnSuccessListener {
                    Log.d("here", "success save")
                    Toast.makeText(mainActivity, "목록에 추가되었습니다", Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).replaceFragment(listFragment)
                }
        }

        binding.showRoute.setOnClickListener {
            if (viewModel.start.value!!.isNotBlank() && viewModel.dest.value!!.isNotBlank()) {
                //경로 표시
                thread(start = true) {
                    val tMapPointStart = TMapPoint(
                        viewModel.startLatitude.value!!.toDouble(),
                        viewModel.startLongitude.value!!.toDouble()
                    )
                    val tMapPointEnd = TMapPoint(
                        viewModel.destLatitude.value!!.toDouble(),
                        viewModel.destLongitude.value!!.toDouble()
                    )
                    try {
                        val tMapPolyLine = TMapData().findPathData(tMapPointStart, tMapPointEnd)
                        tMapPolyLine.lineColor = Color.RED
                        tMapPolyLine.lineWidth = 5.0f
                        tMapView.addTMapPath(tMapPolyLine)
//                        tMapView.addTMapPolyLine("line", tMapPolyLine)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                Toast.makeText(mainActivity, "출발지 또는 도착지를 선택하지 않았습니다", Toast.LENGTH_SHORT).show()
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
                viewModel.memberNum.value =
                    binding.spinnerNum.getItemAtPosition(position).toString()
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
                viewModel.memberGender.value =
                    binding.spinnerGender.getItemAtPosition(position).toString()
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
            .addOnSuccessListener {
                nameList.clear()
                for (document in it) {
                    val place = Place(
                        document["id"] as String,
                        document["address"] as String,
                        document["latitude"] as String,
                        document["longitude"] as String
                    )
                    nameList.add(document["id"] as String)
                    placeList.add(place)
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
                    viewModel.start.value =
                        binding.spinnerStart.getItemAtPosition(position).toString()
                    viewModel.startLongitude.value = placeList[position].longitude
                    viewModel.startLatitude.value = placeList[position].latitude
                    binding.textStart.text = placeList[position].address
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
                    viewModel.dest.value =
                        binding.spinnerDest.getItemAtPosition(position).toString()
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
            viewModel.time.value = SimpleDateFormat("HH:mm").format(cal.time)
        }

        TimePickerDialog(
            mainActivity, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }
}