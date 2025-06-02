package com.eatdel.eattoplan.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.ui.login.LoginActivity
import com.eatdel.eattoplan.ui.photo.PhotoAnalysisActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val intent = Intent(this, PhotoAnalysisActivity::class.java).apply {
                putExtra("IMAGE_URI", it.toString())
            }
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.analyzeFoodBtn).setOnClickListener {
            selectImageLauncher.launch("image/*") // 갤러리 열기
        }
    }
}