package com.ivan.m.reddittimeline.ui.home

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivan.m.reddittimeline.databinding.ItemListContentBinding

class PostViewHolder(
    binding: ItemListContentBinding
) : RecyclerView.ViewHolder(binding.root) {
    val authorView: TextView = binding.authorText
    val contentView: TextView = binding.content
    val timeStamp: TextView = binding.timeStamp
    val thumbnailView: ImageView = binding.itemImage
}