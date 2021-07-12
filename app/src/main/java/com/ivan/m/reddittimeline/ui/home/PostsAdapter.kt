package com.ivan.m.reddittimeline.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.ivan.m.reddittimeline.R
import com.ivan.m.reddittimeline.databinding.ItemListContentBinding
import com.ivan.m.reddittimeline.model.ui.ListItem

class PostsAdapter(
    private val onClickListener: View.OnClickListener,
    private val onContextClickListener: View.OnContextClickListener
) : PagingDataAdapter<ListItem, PostViewHolder>(UIMODEL_COMPARATOR) {

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.authorView.text = item?.author
        holder.contentView.text = item?.title
        holder.timeStamp.text = item?.created.toString()
        holder.thumbnailView.load(item?.thumbnail) {
            crossfade(true)
            placeholder(R.drawable.ic_image_100)
            error(R.drawable.ic_image_100)
        }
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