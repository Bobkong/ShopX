package com.squareup.shopx.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.shopx.R
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.utils.UIUtils

class AppIntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.from_right,R.anim.out_left);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_intro)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)
        setGuideImageHeight()
    }

    private fun setGuideImageHeight() {
        val guideImage = findViewById<ImageView>(R.id.intro_img)
        val imageLayoutParams = guideImage?.layoutParams as ConstraintLayout.LayoutParams
        imageLayoutParams.height = (UIUtils.getHeight(this) * 0.54).toInt()
        guideImage.layoutParams = imageLayoutParams
    }
}