package com.squareup.shopx.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.RoundRectImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.util.*

class NotificationActivity : AppCompatActivity() {
    private val TAG = "NotificationActivity"

    private lateinit var noNotification: TextView
    private lateinit var enableNotification: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        noNotification = findViewById(R.id.no_notification)
        enableNotification = findViewById(R.id.confirm)

        noNotification.setOnClickListener {
            gotoHome(false)
        }

        enableNotification.setOnClickListener {
            gotoHome(true)
        }

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this, true)



    }

    private fun gotoHome(enableNotification: Boolean) {

        ShopXApiService.getInstance().addCustomer(intent.getStringExtra("phone")!!, intent.getStringExtra("nickname")!!, intent.getStringExtra("password")!!, if (enableNotification)  1 else 0)
            .subscribe(object : Observer<AddCustomerResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: AddCustomerResponse?) {
                    if (value?.code == 0) {
                        PreferenceUtils.setUserPhone(intent.getStringExtra("phone")!!)
                        PreferenceUtils.setUsername(intent.getStringExtra("nickname")!!)
                        PreferenceUtils.setNotification(enableNotification)

                        val intent = Intent(this@NotificationActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@NotificationActivity, value?.msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@NotificationActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })

    }


}