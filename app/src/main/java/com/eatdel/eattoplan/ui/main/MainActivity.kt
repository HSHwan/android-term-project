// app/src/main/java/com/eatdel/eattoplan/ui/main/MainActivity.kt
package com.eatdel.eattoplan.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val db = Firebase.firestore
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

        db.collection("foodplan")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "불러오기 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                // snapshot이 null이거나 빈 리스트가 아닌지 확인
                val docs = snapshot?.documents ?: emptyList()
                Log.d("MainActivity", "Firestore docs size = ${docs.size}")
                val plans = docs.mapNotNull { it.toObject(Plan::class.java) }
                Log.d("MainActivity", "Mapped plans size = ${plans.size}")

                binding.recyclerView.adapter = ResultAdapter(
                    items = plans
                ) { plan ->
                    // 클릭된 Plan을 Detail 액티비티로 넘김
                    val intent = Intent(this, PlanDetailActivity::class.java).apply {
                        putExtra(PlanDetailActivity.EXTRA_TITLE, plan.title)
                        putExtra(PlanDetailActivity.EXTRA_RESTAURANT_NAME, plan.restaurant_name)
                        putExtra(PlanDetailActivity.EXTRA_PLACE_ID, plan.place_id)
                        putExtra(PlanDetailActivity.EXTRA_MEET_DATE, plan.meet_date)
                        putExtra(PlanDetailActivity.EXTRA_CONTACT_INFO, plan.contact_info)
                        putExtra(PlanDetailActivity.EXTRA_UID, plan.uid)
                    }
                    startActivity(intent)
                }

            }

        // 4) 버튼 클릭 리스너 설정

        binding.btnAnalyzeFood.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_bookmark -> {
                // 즐겨찾기 화면으로 이동
                startActivity(Intent(this, BookmarkActivity::class.java))
            }
            R.id.nav_saved -> {
                // 저장된 계획 화면으로 이동
                startActivity(Intent(this, MainActivity::class.java))
            }
            // 구글 플레이스로 이동
            R.id.nav_places_search -> {
                startActivity(Intent(this, PlacesSearchActivity::class.java))
            }
        }
        // 메뉴 선택 후 드로어(사이드바) 닫기
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 툴바 오버플로우에만 main_menu.xml 을 inflate
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 1) 먼저 토글(햄버거 메뉴) 처리
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        // 2) 그 다음에 우리 오버플로우 메뉴 처리
        return when (item.itemId) {
            R.id.action_places -> {
                Log.d("MainActivity", "Places 메뉴 클릭!")
                startActivity(Intent(this, PlacesSearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




}
