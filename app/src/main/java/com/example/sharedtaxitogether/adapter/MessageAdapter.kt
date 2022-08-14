package com.example.sharedtaxitogether.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedtaxitogether.R
import com.example.sharedtaxitogether.databinding.ItemRecyclerMessageBinding
import com.example.sharedtaxitogether.model.Chat
import com.example.sharedtaxitogether.model.Profile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MessageAdapter(private val context: Context, shareUid: String) :
    RecyclerView.Adapter<MessageAdapter.MessageItemViewHolder>() {
    private val db = FirebaseFirestore.getInstance()

    //    private val shareInfo: Share = db.collection("shares").document().get() as Share
    val comments = mutableListOf<Chat.Comment>()
    private var profile: Profile? = null

    init {
        // todo 채팅에 참여한 사람 정보 db에서 가져오기
        db.collection("shares").document(shareUid).collection("chat")
            .orderBy("time")
            .addSnapshotListener { value, error ->
                comments.clear()

                for (snapshot in value!!.documents) {
                    val item = Chat.Comment(
                        snapshot.getString("uid"),
                        snapshot.getString("nickname"),
                        snapshot.getString("message"),
                        snapshot.getString("time")
                    )
                    comments.add(item)
                }
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {
        return MessageItemViewHolder(
            ItemRecyclerMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageItemViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int {
        Log.d("size", comments.size.toString())
        return comments.size
    }

    inner class MessageItemViewHolder(private val binding: ItemRecyclerMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat.Comment) {
            if (chat.uid.equals(Firebase.auth.currentUser!!.uid)) {
                binding.messageItemProfile.visibility = View.INVISIBLE
                binding.commentLinearLayout.gravity = Gravity.RIGHT
            } else {
                binding.commentLinearLayout.gravity = Gravity.LEFT
                binding.messageItemProfile.setImageResource(R.drawable.default_profile)
            }
            binding.messageItemTime.text = chat.time?.slice(11..15)
            binding.messageItemName.text = chat.nickname
            binding.messageItemMessage.text = chat.message
        }
    }
}