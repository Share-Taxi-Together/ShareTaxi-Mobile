package com.example.sharedtaxitogether.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedtaxitogether.R
import com.example.sharedtaxitogether.databinding.ItemRecyclerProfilesBinding
import com.example.sharedtaxitogether.model.Share

class ProfileAdapter(private val context: Context, profiles: HashMap<String, Share.Participant>) :
    RecyclerView.Adapter<ProfileAdapter.ProfileItemViewHolder>() {
    private var currentList = mutableListOf(HashMap<String, Share.Participant>())

    init {
        currentList.clear()
        currentList.addAll(listOf(profiles))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileAdapter.ProfileItemViewHolder {
        return ProfileItemViewHolder(
            ItemRecyclerProfilesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileAdapter.ProfileItemViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class ProfileItemViewHolder(private val binding: ItemRecyclerProfilesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HashMap<String, Share.Participant>, position: Int) {
//            println(item.participants.size)
            binding.profileItemProfile.setImageResource(R.drawable.default_profile)
            binding.profileItemNickname.text = item[position.toString()]?.nickname
//            binding.profileItemNickname.text = item.participants[i.toString()]!!.nickname
        }
    }
}
