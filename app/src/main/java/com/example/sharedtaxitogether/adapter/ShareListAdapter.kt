package com.example.sharedtaxitogether.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedtaxitogether.databinding.ItemRecyclerListBinding
import com.example.sharedtaxitogether.model.Share
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class ShareListAdapter(private val context: Context) :
    RecyclerView.Adapter<ShareListAdapter.ListItemViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    private var currentList = mutableListOf<Share>()

    private val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm").format(System.currentTimeMillis())
    // 데이터 가져올 때 출발시간이 현재시간 이후인 데이터만 가져오기
    init {
        db.collection("shares")
            .orderBy("departTime")
            .addSnapshotListener { querySnapshot, exception ->
                currentList.clear()

                for (snapshot in querySnapshot!!.documents) {
                    val date = snapshot["departTime"].toString()
                    if(date > currentTime){
                        val item = snapshot.toObject(Share::class.java)
                        currentList.add(item!!)
                    }
                }
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(
            ItemRecyclerListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class ListItemViewHolder(private val binding: ItemRecyclerListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Share) {
//            binding.listCreatorNickname.text = item.creatorNickname
            when(item.memberGender){
                "All" -> binding.listGender.text = "무관"
                "Female" -> binding.listGender.text = "여자만"
                "Male" -> binding.listGender.text = "남자만"
            }
//            binding.listGender.text = item.memberGender
            binding.listStart.text = item.place["start"]?.id
            binding.listDest.text = item.place["dest"]?.id
            //Todo participants 확인하기
            if (item.participants.isNotEmpty()) {
                Log.d("here participants", item.participants["1"]?.nickname.toString())
            }
            binding.listMember.text = "${item.memberCount} / ${item.memberNum}"
            binding.listTime.text = item.departTime.slice(11..15)

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Share, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}