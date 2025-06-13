package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.Bookmark
import com.eatdel.eattoplan.databinding.ItemBookmarkBinding

class BookmarkAdapter(
    private var items: List<Bookmark> = emptyList(),
    private val onUnbookmarkClick: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    inner class BookmarkViewHolder(val binding: ItemBookmarkBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    //
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bm = items[position]
        with(holder.binding) {
            tvName.text    = bm.name
            tvRating.text  = String.format("%.1f", bm.rating)
            tvAddress.text = bm.address
            tvPhone.text   = bm.phoneNumber

            // 지워진 후에는 리스트에서 삭제
            ivUnbookmark.setOnClickListener {
                onUnbookmarkClick(bm)
            }
        }
    }

    /** 외부에서 데이터 갱신할 때 호출 */
    fun submitList(newItems: List<Bookmark>) {
        items = newItems
        notifyDataSetChanged()
    }

    /** 특정 아이템만 리스트에서 제거(애니메이션 없이) */
    fun removeItem(bm: Bookmark) {
        val idx = items.indexOf(bm)
        if (idx >= 0) {
            items = items.toMutableList().apply { removeAt(idx) }
            notifyItemRemoved(idx)
        }
    }
}
