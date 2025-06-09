package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.PlaceItem
import com.eatdel.eattoplan.databinding.ItemPlaceBinding

/**
 * PlaceItem 리스트를 RecyclerView에 바인딩해주는 어댑터
 */
class PlacesAdapter(
    private var items: List<PlaceItem>,
    private val onClick: (PlaceItem) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceItem) {
            binding.tvName.text = item.name
            binding.tvAddress.text = item.address
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * 새 데이터를 받아서 갱신할 때 호출하세요.
     */
    fun submitList(newItems: List<PlaceItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
