package com.squareup.shopx.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.shopx.R
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.utils.UIUtils


class GuideActivity: AppCompatActivity(), ViewPager.OnPageChangeListener {

    val TAG = "GuideActivity"
    private var mviewList: ArrayList<View>? = null
    private var mViewPager: ViewPager? = null
    private var mDotList:ArrayList<ImageView>? = null
    private var mLastPosition: Int = 0
    private var mContinueButton: ImageView? = null
    private var jumpToSignup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        initView()
        initViewPager()
        initDots()

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)

        mContinueButton = findViewById(R.id.guide_continue)
        mContinueButton?.setOnClickListener {
            if (mLastPosition + 1 != mDotList?.size) {
                mViewPager?.currentItem = mLastPosition + 1
            } else {
                startSignUpActivity()
            }
        }
        PreferenceUtils.setFirstUse(false)

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mLastPosition + 1 == mDotList?.size && isDragPage && positionOffsetPixels == 0) {   //当前页是最后一页，并且是拖动状态，并且像素偏移量为0

            if (!jumpToSignup) {
                jumpToSignup = true
                startSignUpActivity()
            }
        }

    }

    override fun onPageSelected(position: Int) {
        setCurrentDotPosition(position)
    }

    private fun setCurrentDotPosition(position: Int) {
        mDotList?.get(position)?.isEnabled = true
        mDotList?.get(mLastPosition)?.isEnabled = false

        val newLayoutParams = mDotList?.get(position)?.layoutParams as RelativeLayout.LayoutParams
        newLayoutParams.width = UIUtils.dp2px(this, 28f)
        mDotList?.get(position)?.layoutParams = newLayoutParams

        val lastLayoutParams = mDotList?.get(mLastPosition)?.layoutParams as RelativeLayout.LayoutParams
        lastLayoutParams.width = UIUtils.dp2px(this, 8f)
        mDotList?.get(mLastPosition)?.layoutParams = lastLayoutParams

        mLastPosition = position
    }

    var isDragPage = false
    override fun onPageScrollStateChanged(state: Int) {
        isDragPage = state == 1
    }

    internal class MyPagerAdapter(//滑动页的适配器
        private val mImageViewList: List<View>, context: Context
    ) : PagerAdapter() {
        private val mContext: Context

        init {
            mContext = context
        }

        override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
            container.addView(mImageViewList[position])
            return mImageViewList[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            if (mImageViewList.isNotEmpty()) {
                container.removeView(mImageViewList[position])
            }
        }

        override fun getCount(): Int {
            return mImageViewList?.size ?: 0
        }

        override fun isViewFromObject(@NonNull view: View, @NonNull o: Any): Boolean {
            return view === o
        }
    }

    private fun initView() {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        mviewList = ArrayList()
        val guideView1 = inflater.inflate(R.layout.guide1_layout, null)
        setGuideImageHeight(guideView1)
        mviewList?.add(guideView1)

        val guideView2 = inflater.inflate(R.layout.guide2_layout, null)
        setGuideImageHeight(guideView2)
        mviewList?.add(guideView2)

        val guideView3 = inflater.inflate(R.layout.guide3_layout, null)
        setGuideImageHeight(guideView3)
        mviewList?.add(guideView3)

        val guideView4 = inflater.inflate(R.layout.guide4_layout, null)
        setGuideImageHeight(guideView4)
        mviewList?.add(guideView4)

    }

    private fun setGuideImageHeight(guideView: View?) {
        val guideImage = guideView?.findViewById<ImageView>(R.id.guide_image)
        val imageLayoutParams = guideImage?.layoutParams as ConstraintLayout.LayoutParams
        imageLayoutParams.height = (UIUtils.getHeight(this) * 0.67).toInt()
        guideImage.layoutParams = imageLayoutParams
    }

    private fun initViewPager() {
        mViewPager = findViewById<View>(R.id.viewpager) as ViewPager
        mviewList?.let {
            val adapter = MyPagerAdapter(it, this)
            mViewPager!!.adapter = adapter
            mViewPager!!.addOnPageChangeListener(this)
        }
    }

    private fun initDots() {
        val dotsLayout = findViewById<RelativeLayout>(R.id.ll_dots_layout)
        mDotList = ArrayList()
        for (i in mviewList!!.indices) {
            mDotList?.add(dotsLayout.getChildAt(i) as ImageView)
            mDotList?.get(i)?.isEnabled = false
        }
        mLastPosition = 0
        mDotList?.get(0)?.isEnabled = true
    }

    private fun startSignUpActivity() {
        val intent = Intent(this@GuideActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

}