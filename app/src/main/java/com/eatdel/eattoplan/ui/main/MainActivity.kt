package com.eatdel.eattoplan.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.adapter.ResultAdapter
import com.eatdel.eattoplan.data.Plan
import com.eatdel.eattoplan.databinding.ActivityMainBinding
import com.eatdel.eattoplan.ui.bookmark.BookmarkActivity
import com.eatdel.eattoplan.ui.detail.PlanDetailActivity
import com.eatdel.eattoplan.ui.photo.PhotoAnalysisActivity
import com.eatdel.eattoplan.ui.places.PlacesSearchActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 유저가 없습니다.")

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, PhotoAnalysisActivity::class.java)
            intent.putExtra("imageUri", uri)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 + 드로어
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.drawer_open, R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 계획 DB 관리
        db.collection("users")
            .document(userId)
            .collection("plans")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Toast.makeText(this, "불러오기 실패: ${err.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                val list = snap?.toObjects(Plan::class.java) ?: emptyList()
                binding.recyclerView.adapter = ResultAdapter(list) { plan ->
                    val intent = Intent(this, PlanDetailActivity::class.java).apply {
                        putExtra(PlanDetailActivity.EXTRA_RESTAURANT_NAME, plan.name)
                        putExtra(PlanDetailActivity.EXTRA_ADDRESS,         plan.address)
                        putExtra(PlanDetailActivity.EXTRA_MEET_DATE,       plan.meet_date)
                        putExtra(PlanDetailActivity.EXTRA_MEMO,            plan.memo)
                    }
                    startActivity(intent)
                }
            }

        binding.btnAnalyzeFood.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_bookmark       -> startActivity(Intent(this, BookmarkActivity::class.java))
            R.id.nav_saved          -> {/* 현재 이 화면이 바로 저장된 계획 화면이므로 넘기지 않음 */}

            R.id.nav_places_search  -> openPlaceSearch("") // 괄호 안에 머신러닝 결과 데이터 넣기
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openPlaceSearch(foodName: String) {
        val intent = Intent(this, PlacesSearchActivity::class.java).apply {
            putExtra(PlacesSearchActivity.EXTRA_QUERY, foodName)
        }
        startActivity(intent)
    }
}
