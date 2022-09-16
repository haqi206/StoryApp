package com.example.submission1.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission1.data.StoriesResult
import com.example.submission1.databinding.ItemRowStoryBinding

class StoryPagingAdapter :
    PagingDataAdapter<StoriesResult, StoryPagingAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: StoryPagingAdapter.OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: StoryPagingAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener{
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoriesResult) {
            binding.tvItemNama.text = data.name
            Glide.with(itemView.context)
                .load(data.photo)
                .into(binding.imgItemPhoto)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: StoriesResult)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesResult>() {
            override fun areItemsTheSame(oldItem: StoriesResult, newItem: StoriesResult): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoriesResult, newItem: StoriesResult): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}