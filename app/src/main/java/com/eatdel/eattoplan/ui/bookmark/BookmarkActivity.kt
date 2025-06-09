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
// ---------------------------------- 북마크 눌렀을 때 작동할 함수 ----------------------------------
                    // 추후에 해당 식당 구글 플레이스로 이동 추가
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "즐겨찾기 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------------------------- 북마크 리스트 눌렀을 때 작동할 함수 ----------------------------------
}
