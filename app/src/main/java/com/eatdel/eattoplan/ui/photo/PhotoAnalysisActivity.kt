package com.eatdel.eattoplan.ui.photo

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.ui.places.PlacesSearchActivity
import com.eatdel.eattoplan.util.ImageClassifier
import java.io.InputStream

class PhotoAnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        if (imageUri != null) {
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(imageStream)

            val classifier = ImageClassifier(this)
            val result = classifier.classify(bitmap)

            val intent = Intent(this, PlacesSearchActivity::class.java)
            intent.putExtra("foodName", result)
            startActivity(intent)
            finish()
        } else {
            finish() // 오류 처리
        }
    }
}