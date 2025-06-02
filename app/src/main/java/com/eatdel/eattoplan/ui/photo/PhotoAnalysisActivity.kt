package com.eatdel.eattoplan.ui.photo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.ui.main.MainActivity
import com.eatdel.eattoplan.util.ImageClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoAnalysisActivity : AppCompatActivity() {

    private lateinit var imageClassifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_analysis)

        imageClassifier = ImageClassifier(this)

        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        if (imageUri != null) {
            lifecycleScope.launch {
                val bitmap = uriToBitmap(imageUri)
                val result = withContext(Dispatchers.Default) {
                    imageClassifier.classify(bitmap)
                }
                moveToNextActivity(result)
            }
        } else {
            Toast.makeText(this, "이미지 로딩 실패", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(contentResolver, uri)
    }

    private fun moveToNextActivity(keyword: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("FOOD_KEYWORD", keyword)
        }
        startActivity(intent)
        finish()
    }
}
