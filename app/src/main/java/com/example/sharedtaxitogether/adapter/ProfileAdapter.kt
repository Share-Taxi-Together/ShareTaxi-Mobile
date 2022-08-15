package com.example.sharedtaxitogether.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharedtaxitogether.R
import com.example.sharedtaxitogether.databinding.ItemRecyclerProfilesBinding
import com.example.sharedtaxitogether.model.Share

class ProfileAdapter(private val context: Context, profileList: List<Share.Participant>) :
    RecyclerView.Adapter<ProfileAdapter.ProfileItemViewHolder>() {
    private var profiles = profileList
//    private var profiles = mutableListOf<Share.Participant>()

//    init {
//        profiles.clear()
//        profiles.addAll(listOf(profileList))
//    }

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
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    inner class ProfileItemViewHolder(private val binding: ItemRecyclerProfilesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: Share.Participant) {
            if (profile.imgUrl!!.isBlank()) binding.profileItemProfile.setImageResource(R.drawable.default_profile)
            else {
                Glide.with(binding.profileItemProfile.context)
                    .load(profile.imgUrl)
                    .into(binding.profileItemProfile)
            }
            binding.profileItemNickname.text = profile.nickname
        }
    }
}
