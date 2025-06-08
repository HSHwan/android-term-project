// app/src/main/java/com/eatdel/eattoplan/ui/bookmark/BookmarkActivity.kt
package com.eatdel.eattoplan.ui.bookmark

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.adapter.BookmarkAdapter
import com.eatdel.eattoplan.data.Bookmark
import com.eatdel.eattoplan.databinding.ActivityBookmarkBinding
import com.eatdel.eattoplan.ui.detail.BookmarkDetailActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기
        setSupportActionBar(binding.toolbarBookmark)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarBookmark.setNavigationOnClickListener { finish() }

        // RecyclerView 세팅
        binding.rvBookmark.layoutManager = LinearLayoutManager(this)
        loadBookmarks()
    }

    private fun loadBookmarks() {
        db.collection("Bookmark")  // Firestore 컬렉션명
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents
                    .mapNotNull { it.toObject(Bookmark::class.java) }
                binding.rvBookmark.adapter = BookmarkAdapter(list) { bm ->
                    // 클릭 시 상세 화면으로 전달
                    val intent = Intent(this, BookmarkDetailActivity::class.java).apply {
                        putExtra(BookmarkDetailActivity.EXTRA_UID, bm.uid)
                        putExtra(BookmarkDetailActivity.EXTRA_RESTAURANT, bm.restaurant_name)
                        putExtra(BookmarkDetailActivity.EXTRA_PLACE_ID, bm.place_id)
                        putExtra(BookmarkDetailActivity.EXTRA_RATE, bm.rate)
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "즐겨찾기 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
