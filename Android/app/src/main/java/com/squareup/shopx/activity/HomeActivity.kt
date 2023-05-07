package com.squareup.shopx.activity

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.squareup.shopx.R

class HomeActivity : AppCompatActivity(), BottomNavigationBar.OnTabSelectedListener {
    val TAG = "HomeActivity"
    lateinit var navigationBarView: BottomNavigationBar
    var mainFragment: MainFragment? = null

    var x1 = 0f
    var x2 = 0f
    var y1 = 0f
    var y2 = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.squareup.shopx.R.layout.activity_home)

        setDefaultFragment()
        initNavigationBar()
    }

    private fun initNavigationBar() {
        navigationBarView = findViewById(R.id.bottom_navigation)
        navigationBarView.setTabSelectedListener(this)
        navigationBarView.setMode(BottomNavigationBar.MODE_FIXED)
        navigationBarView.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
        navigationBarView
            .addItem(BottomNavigationItem(R.drawable.marker_normal, "Home").setActiveColorResource(R.color.primary_blue_50))
            .addItem(BottomNavigationItem(R.drawable.marker_normal, "Loyalty").setActiveColorResource(R.color.primary_blue_50))
            .addItem(BottomNavigationItem(R.drawable.marker_normal, "Orders").setActiveColorResource(R.color.primary_blue_50))
            .addItem(BottomNavigationItem(R.drawable.marker_normal, "Settings").setActiveColorResource(R.color.primary_blue_50))
            .setFirstSelectedPosition(0)
            .initialise()

    }

    private fun setDefaultFragment() {
        val fm: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        mainFragment = MainFragment()
        transaction.replace(R.id.fragment_container, mainFragment!!)
        transaction.commit()
    }

    override fun onTabSelected(position: Int) {

    }

    override fun onTabUnselected(position: Int) {
    }

    override fun onTabReselected(position: Int) {
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action === MotionEvent.ACTION_DOWN) {
            x1 = event!!.x
            y1 = event.y
        }
        if (event!!.action === MotionEvent.ACTION_UP) {
            x2 = event!!.x
            y2 = event.y
            if (y2 - y1 > 50 && navigationBarView.currentSelectedPosition == 0 && mainFragment?.currentFragment == 0) {
                mainFragment?.changeToMap()
            } else if (y1 - y2 > 50 && navigationBarView.currentSelectedPosition == 0 && mainFragment?.currentFragment == 1) {
                mainFragment?.changeToIntro()
            }
        }
        return super.onTouchEvent(event)
    }

}