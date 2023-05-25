package com.squareup.shopx.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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

class EnrollSuccessActivity : AppCompatActivity() {
    private val TAG = "EnrollSuccessActivity"
    private lateinit var loadingView: ConstraintLayout
    private lateinit var backButton: ImageView
    private lateinit var merchantLogo: ImageView
    private lateinit var merchantName: TextView
    private lateinit var username: TextView
    private lateinit var userPoints: TextView
    private lateinit var plusPoint: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_success)

        loadingView = findViewById(R.id.loading_view)
        merchantLogo = findViewById<RoundRectImageView>(R.id.merchant_logo)
        username = findViewById(R.id.user_name)
        merchantName = findViewById(R.id.merchant_name)
        userPoints = findViewById(R.id.user_points)
        plusPoint = findViewById(R.id.plus_point)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)

        backButton = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val merchantInfo = intent.extras?.getSerializable("merchantInfo") as AllMerchantsResponse.ShopXMerchant
        val loyaltyInfo = intent.extras?.getSerializable("loyaltyInfo") as GetLoyaltyInfoResponse

        enrollLoyaltyProgram(merchantInfo, loyaltyInfo)
    }

    private fun enrollLoyaltyProgram(merchantInfo: ShopXMerchant, loyaltyInfo: GetLoyaltyInfoResponse) {
        // todo: change the test code
        ShopXApiService.getInstance().enrollLoyalty(merchantInfo.accessToken, "+18583190004", loyaltyInfo.loyaltyInfo.program.id)
            .subscribe(object: Observer<EnrollLoyaltyResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: EnrollLoyaltyResponse?) {
                    runOnUiThread {
                        if (value?.code == 1) {
                            loadingView.visibility = View.GONE
                            Toast.makeText(this@EnrollSuccessActivity, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }
                        accumulateTwoPoints(merchantInfo, loyaltyInfo, value!!.loyaltyAccount)
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        loadingView.visibility = View.GONE
                        Toast.makeText(this@EnrollSuccessActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun accumulateTwoPoints(merchantInfo: ShopXMerchant, loyaltyInfo: GetLoyaltyInfoResponse, loyaltyAccount: String) {
        val accumulatePoints = AccumulateLoyaltyPointsRequest.AccumulatePoints(2)
        val request = AccumulateLoyaltyPointsRequest(UUID.randomUUID().toString(), merchantInfo.locationId, accumulatePoints)
        SquareApiService.getInstance(merchantInfo.accessToken).accumulatePoints(loyaltyAccount, request)
            .subscribe(object: Observer<AccumulateLoyaltyPointsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AccumulateLoyaltyPointsResponse?) {
                    runOnUiThread {
                        if (value == null || value.events.isNullOrEmpty()) {
                            plusPoint.visibility = View.GONE
                            userPoints.text = "0 pts"
                        } else {
                            plusPoint.text = "+${value.events?.get(0)?.accumulatePoints?.points.toString()} pts"
                            userPoints.text = "${value.events?.get(0)?.accumulatePoints?.points.toString()} pts"
                        }
                        loadingView.visibility = View.GONE
                        Glide.with(this@EnrollSuccessActivity).load(merchantInfo.logoUrl).into(merchantLogo)
                        var nameString = if (PreferenceUtils.getUsername().length > 1) {
                            PreferenceUtils.getUsername().substring(0, 2)
                        } else {
                            PreferenceUtils.getUsername().substring(0, 1)
                        }
                        nameString = nameString.toUpperCase()
                        username.text = nameString
                        merchantName.text = merchantInfo.businessName
                        EventBus.getDefault().post(
                            RefreshLoyaltyEvent(
                                merchantInfo, value!!.events?.get(0)?.accumulatePoints?.points
                            )
                        )
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        plusPoint.visibility = View.GONE
                        userPoints.text = "0 pts"
                        loadingView.visibility = View.GONE
                        plusPoint.visibility = View.GONE
                        EventBus.getDefault().post(
                            RefreshLoyaltyEvent(
                                merchantInfo, 0
                            )
                        )
                        Toast.makeText(this@EnrollSuccessActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }
}