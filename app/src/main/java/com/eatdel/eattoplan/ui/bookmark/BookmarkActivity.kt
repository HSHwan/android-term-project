package com.eatdel.eattoplan.ui.bookmark

import android.app.AlertDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.adapter.BookmarkAdapter
import com.eatdel.eattoplan.data.Bookmark
import com.eatdel.eattoplan.databinding.ActivityBookmarkBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private val db   = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("로그인된 유저가 없습니다.")

    // 즐겨찾기 항목
    private val bookmarkedIds = mutableSetOf<String>()
    // 계획 저장된 항목(placeId)
    private val savedIds      = mutableSetOf<String>()

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
        adapter = BookmarkAdapter(
            savedIds         = savedIds,
            onUnbookmarkClick = { bm -> removeBookmark(bm) },
            onPlanToggle      = { bm, add -> togglePlan(bm, add) }
        )
        binding.rvBookmark.adapter = adapter

        // 1) 즐겨찾기 로드 → adapter.submitList
        loadBookmarks()
        // 2) 계획 로드 → savedIds 채우기
        loadSavedPlans()
    }

    private fun loadBookmarks() {
        db.collection("users")
            .document(userId)
            .collection("bookmarks")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents
                    .mapNotNull { it.toObject(Bookmark::class.java) }
                bookmarkedIds.clear()
                bookmarkedIds.addAll(list.map { it.place_id })
                adapter.submitList(list)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,
                    "즐겨찾기 불러오기 실패: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeBookmark(bm: Bookmark) {
        db.collection("users")
            .document(userId)
            .collection("bookmarks")
            .document(bm.place_id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this,
                    "즐겨찾기에서 제거되었습니다",
                    Toast.LENGTH_SHORT).show()
                // 화면에서 제거
                adapter.removeItem(bm)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,
                    "제거 실패: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadSavedPlans() {
        db.collection("users")
            .document(userId)
            .collection("plans")
            .get()
            .addOnSuccessListener { snap ->
                savedIds.clear()
                snap.documents.forEach { savedIds.add(it.id) }
                // 아이콘 초기 상태 반영
                adapter.updatePlanState("", false)  // 빈 호출: single item 갱신 대신 전체 갱신
                adapter.notifyDataSetChanged()
            }
    }

    private fun togglePlan(bm: Bookmark, add: Boolean) {
        val ref = db.collection("users")
            .document(userId)
            .collection("plans")
            .document(bm.place_id)

        if (add) {
            // 다이얼로그로 메모·날짜 입력
            val v = layoutInflater.inflate(R.layout.dialog_plan, null)
            val etMemo = v.findViewById<EditText>(R.id.etMemo)
            val dp     = v.findViewById<DatePicker>(R.id.datePicker)
            // 기본 날짜 오늘로
            val cal = Calendar.getInstance()
            dp.updateDate(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

            AlertDialog.Builder(this)
                .setTitle("계획 추가")
                .setView(v)
                .setPositiveButton("저장") { _, _ ->
                    val memo = etMemo.text.toString().trim()
                    val date = "${dp.year}-${dp.month+1}-${dp.dayOfMonth}"
                    // 저장할 데이터
                    val data = mapOf(
                        "placeId"  to bm.place_id,
                        "name"     to bm.name,
                        "address"  to bm.address,
                        "memo"     to memo,
                        "meet_date" to date
                    )
                    ref.set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this,
                                "계획에 추가되었습니다",Toast.LENGTH_SHORT).show()
                            adapter.updatePlanState(bm.place_id, true)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,
                                "저장 실패: ${it.message}",Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("취소", null)
                .show()

        } else {
            // 제거
            ref.delete()
                .addOnSuccessListener {
                    Toast.makeText(this,
                        "계획에서 제거되었습니다",Toast.LENGTH_SHORT).show()
                    adapter.updatePlanState(bm.place_id, false)
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                        "제거 실패: ${it.message}",Toast.LENGTH_SHORT).show()
                }
        }
    }
}