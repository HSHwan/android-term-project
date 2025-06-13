package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.data.PlaceItem
import com.eatdel.eattoplan.databinding.ItemPlaceBinding

class PlaceAdapter(
    private var items: List<PlaceItem>,
    private val bookmarkedIds: MutableSet<String>,
    private val savedIds: MutableSet<String>,
    private val onBookmarkClick: (PlaceItem, Boolean) -> Unit,
    private val onSaveClick: (PlaceItem, Boolean) -> Unit,
    private val onItemClick:     (PlaceItem) -> Unit

) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {
    //private var items: List<PlaceItem> = emptyList()
    val currentItems: List<PlaceItem> get() = items

    inner class PlaceViewHolder(val binding: ItemPlaceBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = items[position]
        with(holder.binding) {
            tvName.text = place.name
            tvAddress.text = place.address

            // ★ rating & reviewCount 텍스트 구성
            if (place.rating != null) {
                val ratingStr = String.format("%.1f", place.rating)
                val reviewStr = "${place.reviewCount}개 리뷰"
                tvRatingCount.apply {
                    text = "$ratingStr · $reviewStr"
                    visibility = View.VISIBLE
                }
            } else {
                tvRatingCount.visibility = View.GONE
            }


            // 1) 아이콘 초기 상태
            val isBook = bookmarkedIds.contains(place.place_id)
            ivBookmark.setImageResource(
                if (isBook) R.drawable.ic_bookmark else R.drawable.ic_bookmark_add
            )
            val isSave = savedIds.contains(place.place_id)
            ivSavePlan.setImageResource(
                if (isSave) R.drawable.ic_plan_delete else R.drawable.ic_plan_add
            )

            // 2) 클릭 리스너: 상태 토글 + 호출자에게 알림
            ivBookmark.setOnClickListener {
                val newState = !isBook
                onBookmarkClick(place, newState)
                ivBookmark.setImageResource(
                    if (newState) R.drawable.ic_bookmark_add
                    else R.drawable.ic_bookmark
                )
            }
            ivSavePlan.setOnClickListener {
                val newState = !isSave
                onSaveClick(place, newState)
                ivSavePlan.setImageResource(
                    if (newState) R.drawable.ic_plan_add
                    else R.drawable.ic_plan_delete
                )
            }

            root.setOnClickListener {
                val starText = place.rating?.let { String.format("%.1f", it) } ?: "정보 없음"
                val countText = "${place.reviewCount}개 리뷰"
                Toast.makeText(
                    root.context,
                    "${place.name}\n별점: $starText\n$countText",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount() = items.size

    // 리스트 갱신 메서드
    fun submitList(newItems: List<PlaceItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
