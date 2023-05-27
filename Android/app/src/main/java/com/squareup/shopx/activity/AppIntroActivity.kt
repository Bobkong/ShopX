package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.SignInAccount
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

        findViewById<TextView>(R.id.sign_up).setOnClickListener {
            val intent = Intent(this@AppIntroActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.sign_in).setOnClickListener {
            val intent = Intent(this@AppIntroActivity, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setGuideImageHeight() {
        val guideImage = findViewById<ImageView>(R.id.intro_img)
        val imageLayoutParams = guideImage?.layoutParams as ConstraintLayout.LayoutParams
        imageLayoutParams.height = (UIUtils.getHeight(this) * 0.54).toInt()
        guideImage.layoutParams = imageLayoutParams
    }
}