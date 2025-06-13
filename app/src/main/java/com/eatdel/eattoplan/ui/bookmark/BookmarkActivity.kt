package com.eatdel.eattoplan.ui.bookmark

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.adapter.BookmarkAdapter
import com.eatdel.eattoplan.data.Bookmark
import com.eatdel.eattoplan.databinding.ActivityBookmarkBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private val db   = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 유저가 없습니다.")

    private lateinit var adapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기
        setSupportActionBar(binding.toolbarBookmark)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarBookmark.setNavigationOnClickListener { finish() }

        // RecyclerView + Adapter 초기화
        binding.rvBookmark.layoutManager = LinearLayoutManager(this)
        adapter = BookmarkAdapter(onUnbookmarkClick = { bm ->
            db.collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(bm.place_id)               // ← 여기가 컬렉션이 아닌 문서
                .delete()                           // ← 여기에 delete() 호출
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "즐겨찾기에서 제거되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    // 화면에서 해당 아이템만 제거
                    adapter.removeItem(bm)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "제거 실패: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        })
        binding.rvBookmark.adapter = adapter

        // 처음 로드
        loadBookmarks()
    }

    private fun loadBookmarks() {
        db.collection("users")
            .document(userId)
            .collection("bookmarks")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents
                    .mapNotNull { it.toObject(Bookmark::class.java) }
                adapter.submitList(list)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "즐겨찾기 불러오기 실패: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
