package com.eatdel.eattoplan.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.ui.bookmark.BookmarkActivity
import com.eatdel.eattoplan.util.ListActivity
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.ui.result.ResultActivity
import com.eatdel.eattoplan.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) ViewBinding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2) 툴바 설정 및 Drawer 토글
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 3) NavigationView 클릭 리스너 설정
        binding.navView.setNavigationItemSelectedListener(this)

        // 4) 버튼 클릭 리스너 설정
        binding.btnUploadPhoto.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
        binding.btnBookmark.setOnClickListener {
            startActivity(Intent(this, BookmarkActivity::class.java))
        }
        binding.btnSavedPlans.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }
    }

    // NavigationView.OnNavigationItemSelectedListener 구현
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_bookmark -> {
                startActivity(Intent(this, BookmarkActivity::class.java))
            }
            R.id.nav_saved -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawers()
        return true
    }
}