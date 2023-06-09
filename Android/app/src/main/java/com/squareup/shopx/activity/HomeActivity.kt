package com.squareup.shopx.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.ar.core.codelabs.arlocalizer.helpers.GeoPermissionsHelper
import com.squareup.shopx.BaseApplication
import com.squareup.shopx.R
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.model.CartUpdateEvent
import com.squareup.shopx.model.NotificationEvent
import com.squareup.shopx.utils.BroadcastReceiverPage
import com.squareup.shopx.utils.Transparent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {
    val TAG = "HomeActivity"
    var mainFragment: MainFragment = MainFragment()
    var loyaltyFragment: LoyaltyCardsFragment = LoyaltyCardsFragment()
    var orderFragment: OrderHistoryFragment = OrderHistoryFragment()
    var profileFragment: ProfileFragment = ProfileFragment()
    lateinit var homeTabIcon: ImageView
    lateinit var loyaltyTabIcon: ImageView
    lateinit var orderTabIcon: ImageView
    lateinit var profileTabIcon: ImageView
    lateinit var navigationBar: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.squareup.shopx.R.layout.activity_home)

        homeTabIcon = findViewById(R.id.home_tab)
        loyaltyTabIcon = findViewById(R.id.loyalty_tab)
        orderTabIcon = findViewById(R.id.order_tab)
        profileTabIcon = findViewById(R.id.profile_tab)
        navigationBar = findViewById(R.id.navigation_bar)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this, true)

        // The app must have been given the Location permission. If we don't have it yet, request it.
        if (!GeoPermissionsHelper.hasGeoPermissions(this)) {
            GeoPermissionsHelper.requestPermissions(this)
            return
        }

        setDefaultFragment()
        initNavigationBar()
        EventBus.getDefault().register(this);

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (!GeoPermissionsHelper.hasGeoPermissions(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(this, "Camera and location permissions are needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!GeoPermissionsHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                GeoPermissionsHelper.launchPermissionSettings(this)
            }
            this.finish()
        } else {
            setDefaultFragment()
            initNavigationBar()
        }
    }

    private fun initNavigationBar() {


        homeTabIcon.setOnClickListener {
            val fm: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fm.beginTransaction()
            transaction.replace(R.id.fragment_container, mainFragment)
            transaction.commit()
            homeTabIcon.isSelected = true
            loyaltyTabIcon.isSelected = false
            orderTabIcon.isSelected = false
            profileTabIcon.isSelected = false
        }

        loyaltyTabIcon.setOnClickListener {
            val fm: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fm.beginTransaction()
            transaction.replace(R.id.fragment_container, loyaltyFragment)
            transaction.commit()
            homeTabIcon.isSelected = false
            loyaltyTabIcon.isSelected = true
            orderTabIcon.isSelected = false
            profileTabIcon.isSelected = false
        }

        orderTabIcon.setOnClickListener {
            val fm: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fm.beginTransaction()
            transaction.replace(R.id.fragment_container, orderFragment)
            transaction.commit()
            homeTabIcon.isSelected = false
            loyaltyTabIcon.isSelected = false
            orderTabIcon.isSelected = true
            profileTabIcon.isSelected = false
        }

        profileTabIcon.setOnClickListener {
            val fm: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fm.beginTransaction()
            transaction.replace(R.id.fragment_container, profileFragment)
            transaction.commit()
            homeTabIcon.isSelected = false
            loyaltyTabIcon.isSelected = false
            orderTabIcon.isSelected = false
            profileTabIcon.isSelected = true
        }
    }

    private fun setDefaultFragment() {
        homeTabIcon.isSelected = true
        val fm: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.fragment_container, mainFragment)
        transaction.commit()
    }

    public fun hideNavigationBar() {
        navigationBar.visibility = View.GONE
    }

    public fun showNavigationBar() {
        navigationBar.visibility = View.VISIBLE
    }

    fun goToMainMap() {
        val fm: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.fragment_container, mainFragment)
        transaction.commit()
        homeTabIcon.isSelected = true
        loyaltyTabIcon.isSelected = false
        orderTabIcon.isSelected = false
        profileTabIcon.isSelected = false

        Handler().postDelayed({
            mainFragment.showMap()
        }, 200)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationEvent(event: NotificationEvent) {
        if (BaseApplication.hasNotified == 0) {
            createNotification(event.merchant)
            BaseApplication.hasNotified = 1
        }
    }

    private fun createNotification(merchant: AllMerchantsResponse.ShopXMerchant) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("Notify", "ShopX", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "ShopX Discount Notification"
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            val intent = Intent(this, BroadcastReceiverPage::class.java)
            intent.putExtra("merchant", merchant)
            this.sendBroadcast(intent)
        }
    }

}