package com.example.sharedtaxitogether.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharedtaxitogether.MessageActivity
import com.example.sharedtaxitogether.R
import com.example.sharedtaxitogether.databinding.ItemRecyclerListBinding
import com.example.sharedtaxitogether.model.Share
import com.google.firebase.firestore.FirebaseFirestore

class ShareListAdapter(private val context: Context) :
    RecyclerView.Adapter<ShareListAdapter.ListItemViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    private var currentList = mutableListOf<Share>()

    init{
        db.collection("shares")
            .orderBy("departTime")
            .addSnapshotListener{querySnapshot, exception ->
            currentList.clear()

            for(snapshot in querySnapshot!!.documents){
                val item = snapshot.toObject(Share::class.java)
                currentList.add(item!!)
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(ItemRecyclerListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
            binding.listGender.text = item.memberGender
            binding.listStart.text = item.start
            binding.listDest.text = item.dest
            binding.listMember.text = "${item.memberCount} / ${item.memberNum}"
            binding.listTime.text = item.departTime

//            when(item.creatorGender){
//                "Male" -> binding.listCreatorGender.setImageResource(R.drawable.male)
//                "Female" -> binding.listCreatorGender.setImageResource(R.drawable.female)
//            }

//            if(item.creatorImgUrl.isBlank()){
//                binding.listCreatorProfileImage.setImageResource(R.drawable.default_profile)
//            }else{
//                Glide.with(binding.listCreatorProfileImage.context)
//                    .load(item.creatorImgUrl)
//                    .into(binding.listCreatorProfileImage)
//            }

            itemView.setOnClickListener{
                Intent(context, MessageActivity::class.java).apply {
                    putExtra("data", item)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
        }
    }
}