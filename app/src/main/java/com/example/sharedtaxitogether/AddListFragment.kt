package com.example.sharedtaxitogether

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        Log.d("here onAttach", "onAttach")

        //db에서 장소 정보 가져와 저장장 PlaceLyout(id, address)
        getPlace()

        //합승인원구할건지 여부 확인
        checkAdd()

        binding = FragmentAddBinding.inflate(layoutInflater)

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, nameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerDest.adapter = adapter
    }

    private fun checkAdd() {
//        Log.d("here checkAdd", "호출")
        val builder = AlertDialog.Builder(mainActivity)
        builder.setMessage("합승인원을 구하시겠습니까?")
            .setPositiveButton("네") { _, _ ->

                binding.spinnerDest.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            Log.d("here choiceDest", position.toString())
                            var dest = binding.spinnerDest.getItemAtPosition(position).toString()
                            binding.textDest.text = placeList[position].address
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                binding.spinnerStart.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            Log.d("here choiceStart", position.toString())
                            var start = binding.spinnerStart.getItemAtPosition(position).toString()
                            binding.textStart.text = placeList[position].address
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }


            }
            .setNegativeButton("아니요") { _, _ ->

            }
        builder.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        binding = FragmentAddBinding.inflate(layoutInflater)
//
//        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, nameList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerDest.adapter = adapter
        Log.d("here onCreateView", "onCreateView")


//        binding.choiceStart.setOnClickListener {
//            Log.d("here choiceStart", "호출")
//
//            val dialog = ChoicePlaceDialog(mainActivity, nameList)
//            dialog.myDialog()
//
//            dialog.setOnClickListener(object : ChoicePlaceDialog.OnDialogClickListener {
//                override fun onClicked(position: Int) {
//                    binding.textStart.text = placeList[position].name + placeList[position].address
//                }
//            })
//        }

        binding.spinnerNum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("here num", "호출")

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
                Log.d("here gender", "호출")

                // TODO gender 값 활용하기
                var gender = binding.spinnerGender.getItemAtPosition(position).toString()
            }
        }

        binding.timepicker.setOnClickListener {
            getTime()
        }

        return binding.root
    }

    private fun getPlace() {
        Log.d("here getPlace()", "호출됐어")
        db.collection("places")
            .get()
            .addOnSuccessListener { result ->
                nameList.clear()
                for (document in result) {
                    val item = PlaceLayout(document["id"] as String, document["address"] as String)
                    nameList.add(document["id"] as String)
                    placeList.add(item)
                }
                Log.d("here placeList", placeList[2].name)
                Log.d("here placeList", placeList[2].address)
            }
    }


    @SuppressLint("SimpleDateFormat")
    private fun getTime() {
        Log.d("here getTime()", "호출됐어")

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            binding.timeText.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        TimePickerDialog(
            mainActivity,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

//    private fun showTimePicker() {
//        val cal = Calendar.getInstance()
//        TimePickerDialog(mainActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//            binding.timeText.setText("$hourOfDay:$minute")
//            Toast.makeText(mainActivity, "$hourOfDay:$minute", Toast.LENGTH_SHORT).show()
//        }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
//    }

}