package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keylab.mobile.databinding.ItemPromoBannerBinding

data class PromoBanner(
    val id: Int,
    val title: String,
    val ctaText: String,
    val imageRes: Int = 0
)

class PromoBannerAdapter(
    private val onCtaClick: (PromoBanner) -> Unit
) : ListAdapter<PromoBanner, PromoBannerAdapter.ViewHolder>(PromoBannerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPromoBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onCtaClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemPromoBannerBinding,
        private val onCtaClick: (PromoBanner) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: PromoBanner) {
            binding.tvPromoTitle.text = banner.title
            binding.btnPromoCta.text = banner.ctaText
            
            if (banner.imageRes != 0) {
                binding.ivPromoImage.setImageResource(banner.imageRes)
            }
            
            binding.btnPromoCta.setOnClickListener {
                onCtaClick(banner)
            }
        }
    }

    private class PromoBannerDiffCallback : DiffUtil.ItemCallback<PromoBanner>() {
        override fun areItemsTheSame(oldItem: PromoBanner, newItem: PromoBanner): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PromoBanner, newItem: PromoBanner): Boolean {
            return oldItem == newItem
        }
    }
}
