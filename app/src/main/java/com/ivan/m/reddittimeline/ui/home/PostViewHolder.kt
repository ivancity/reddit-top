package com.ivan.m.reddittimeline.ui.home

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivan.m.reddittimeline.databinding.ItemListContentBinding

class PostViewHolder(
    binding: ItemListContentBinding
) : RecyclerView.ViewHolder(binding.root) {
    val idView: TextView = binding.idText
    val contentView: TextView = binding.content
}