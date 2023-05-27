package com.squareup.shopx.activity

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
import com.squareup.shopx.R
import com.squareup.shopx.model.AccumulateLoyaltyPointsRequest
import com.squareup.shopx.model.AccumulateLoyaltyPointsResponse
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.EnrollLoyaltyResponse
import com.squareup.shopx.model.GetLoyaltyInfoResponse
import com.squareup.shopx.model.RefreshLoyaltyEvent
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.RoundRectImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.util.*

class PaySuccessActivity : AppCompatActivity() {
    private val TAG = "EnrollSuccessActivity"
    private lateinit var loadingView: ConstraintLayout
    private lateinit var backButton: ImageView
    private lateinit var merchantLogo: ImageView
    private lateinit var username: TextView
    private lateinit var plusPoint: TextView
    private lateinit var viewOrder: TextView
    private lateinit var backToHome: TextView
    private lateinit var shareFilterLl: ConstraintLayout
    private lateinit var filterList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_success)

        loadingView = findViewById(R.id.loading_view)
        backButton = findViewById(R.id.back)
        merchantLogo = findViewById(R.id.merchant_logo)
        username = findViewById(R.id.user_name)
        plusPoint = findViewById(R.id.plus_point)
        viewOrder = findViewById(R.id.view_order)
        backToHome = findViewById(R.id.back_to_home)
        shareFilterLl = findViewById(R.id.share_ar_to_ins)
        filterList = findViewById(R.id.filter_list)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this, true)

        backButton = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val merchantInfo = intent.extras?.getSerializable("merchantInfo") as AllMerchantsResponse.ShopXMerchant
        val loyaltyInfo = intent.extras?.getSerializable("loyaltyInfo") as GetLoyaltyInfoResponse


    }


//    private fun accumulateTwoPoints(merchantInfo: ShopXMerchant, loyaltyInfo: GetLoyaltyInfoResponse, loyaltyAccount: String) {
//        val accumulatePoints = AccumulateLoyaltyPointsRequest.AccumulatePoints(2)
//        val request = AccumulateLoyaltyPointsRequest(UUID.randomUUID().toString(), merchantInfo.locationId, accumulatePoints)
//        SquareApiService.getInstance(merchantInfo.accessToken).accumulatePoints(loyaltyAccount, request)
//            .subscribe(object: Observer<AccumulateLoyaltyPointsResponse> {
//                override fun onSubscribe(d: Disposable?) {
//
//                }
//
//                override fun onNext(value: AccumulateLoyaltyPointsResponse?) {
//                    runOnUiThread {
//                        if (value == null || value.events.isNullOrEmpty()) {
//                            plusPoint.visibility = View.GONE
//                            userPoints.text = "0 pts"
//                        } else {
//                            plusPoint.text = "+${value.events?.get(0)?.accumulatePoints?.points.toString()} pts"
//                            userPoints.text = "${value.events?.get(0)?.accumulatePoints?.points.toString()} pts"
//                        }
//                        loadingView.visibility = View.GONE
//                        Glide.with(this@PaySuccessActivity).load(merchantInfo.logoUrl).into(merchantLogo)
//                        var nameString = if (PreferenceUtils.getUsername().length > 1) {
//                            PreferenceUtils.getUsername().substring(0, 2)
//                        } else {
//                            PreferenceUtils.getUsername().substring(0, 1)
//                        }
//                        nameString = nameString.toUpperCase()
//                        username.text = nameString
//                        merchantName.text = merchantInfo.businessName
//                        EventBus.getDefault().post(
//                            RefreshLoyaltyEvent(
//                                merchantInfo, value!!.events?.get(0)?.accumulatePoints?.points
//                            )
//                        )
//                    }
//                }
//
//                override fun onError(e: Throwable?) {
//                    runOnUiThread {
//                        plusPoint.visibility = View.GONE
//                        userPoints.text = "0 pts"
//                        loadingView.visibility = View.GONE
//                        plusPoint.visibility = View.GONE
//                        EventBus.getDefault().post(
//                            RefreshLoyaltyEvent(
//                                merchantInfo, 0
//                            )
//                        )
//                        Toast.makeText(this@PaySuccessActivity, e?.message, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onComplete() {
//                }
//
//            })
//    }
}