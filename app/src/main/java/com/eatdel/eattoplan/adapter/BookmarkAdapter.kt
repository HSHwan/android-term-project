package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.Bookmark
import com.eatdel.eattoplan.databinding.ItemResultBinding

class BookmarkAdapter(
    private val items: List<Bookmark>,
    private val onClick: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.VH>() {

    inner class VH(val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bm: Bookmark) {
            binding.tvItemFoodName.text = bm.restaurant_name
            binding.tvItemDate.text = "평점: ${bm.rate}"
            binding.root.setOnClickListener { onClick(bm) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])
}
