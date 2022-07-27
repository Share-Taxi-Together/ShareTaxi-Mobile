package com.example.sharedtaxitogether

import android.R
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.sharedtaxitogether.databinding.FragmentAddBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddListFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentAddBinding
    private val db = FirebaseFirestore.getInstance()

    private val placeList = arrayListOf<PlaceLayout>()
    private val nameList = arrayListOf<String>()

    // TODO LiveData 합승정보 추가 및 observer
    // todo 출발, 도착 장소 다른지 체크

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        Log.d("here onAttach", "onAttach")

        //db에서 장소 정보 가져와 저장장 PlaceLyout(id, address)
        getPlace()

        //합승인원구할건지 여부 확인
        checkAdd()

        binding = FragmentAddBinding.inflate(layoutInflater)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.spinnerNum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO num 값 활용하기
                var num = binding.spinnerNum.getItemAtPosition(position).toString()
            }
        }

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
                    val item = PlaceLayout(document["id"] as String, document["address"] as String)
                    nameList.add(document["id"] as String)
                    placeList.add(item)
                }
                placeList.add(PlaceLayout("선택", ""))
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

        binding.spinnerStart.setSelection(nameList.size-1)
        binding.spinnerDest.setSelection(nameList.size-1)

        binding.spinnerStart.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var start = binding.spinnerStart.getItemAtPosition(position).toString()
                    binding.textStart.text = placeList[position].address
                    Log.d("here111", position.toString())
                }
            }

        binding.spinnerDest.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var dest = binding.spinnerDest.getItemAtPosition(position).toString()
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

        TimePickerDialog(mainActivity, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

//    private fun showTimePicker() {
//        val cal = Calendar.getInstance()
//        TimePickerDialog(mainActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//            binding.timeText.setText("$hourOfDay:$minute")
//            Toast.makeText(mainActivity, "$hourOfDay:$minute", Toast.LENGTH_SHORT).show()
//        }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
//    }

}