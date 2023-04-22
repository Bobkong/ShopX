package com.squareup.easyshop.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.easyshop.R
import com.squareup.easyshop.consts.Configs
import com.squareup.easyshop.netservice.SquareAPI.SquareApiService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.retrieve_merchant).setOnClickListener {
            SquareApiService.getInstance(Configs.token).retrieveMerchant(Configs.merchantId)
                .subscribe {
                    //PushUtils.registerPushHandler(new MyFirebaseMessagingService());
                    val intent = Intent(this@MainActivity, RetrieveMerchantActivity::class.java)
                    intent.putExtra("merchant", it)
                    startActivity(intent)
                }
        }
    }
}