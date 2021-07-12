package com.ivan.m.reddittimeline.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ivan.m.reddittimeline.databinding.ItemListContentBinding
import com.ivan.m.reddittimeline.model.ui.ListItem

class PostsAdapter(
    private val onClickListener: View.OnClickListener,
    private val onContextClickListener: View.OnContextClickListener
) : PagingDataAdapter<ListItem, PostViewHolder>(UIMODEL_COMPARATOR) {

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.idView.text = item?.id
        holder.contentView.text = item?.title
        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
            setOnContextClickListener(onContextClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemListContentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return (oldItem.id == newItem.id)
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                oldItem == newItem
        }
    }

//    inner class ViewHolder(binding: ItemListContentBinding) : RecyclerView.ViewHolder(binding.root) {
//        val idView: TextView = binding.idText
//        val contentView: TextView = binding.content
//    }
}