 package com.eatdel.eattoplan.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.data.DataStoreManager
import com.eatdel.eattoplan.ui.login.LoginActivity
import com.eatdel.eattoplan.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        dataStoreManager = DataStoreManager(this)

        lifecycleScope.launch {
            val isFirebaseLoggedIn = FirebaseAuth.getInstance().currentUser != null
            val isLocalFlagSet = dataStoreManager.loginStateFlow.first()

            if (isFirebaseLoggedIn && isLocalFlagSet) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }
}