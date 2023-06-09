package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.shopx.R
import com.squareup.shopx.adapter.ARFilterAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.utils.UIUtils
import com.squareup.shopx.widget.ARFilterItemDecoration
import com.squareup.shopx.widget.ARSpanSizeLookup
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.util.*

class PaySuccessActivity : AppCompatActivity() {
    private val TAG = "PaySuccessActivity"
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
        Transparent.transparentStatusBar(this, false)

        backButton = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish()
        }
        backToHome.setOnClickListener {
            val intent = Intent(this@PaySuccessActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val merchantInfo = intent.extras?.getSerializable("merchantInfo") as ShopXMerchant
        val loyaltyInfo = intent.extras?.getSerializable("loyaltyInfo") as? GetLoyaltyInfoResponse
        val orderId = intent.extras?.getSerializable("orderId") as String
        val nonce = intent.extras?.getSerializable("nonce") as String
        val value = intent.extras?.getSerializable("value") as Int
        val orderInfo = intent.extras?.getSerializable("orderInfo") as PlaceOrderRequest
        val arItemList = intent.extras?.getSerializable("arItems") as ARItemList
        payOrder(merchantInfo, value, nonce, orderId, loyaltyInfo)

        viewOrder.setOnClickListener {
            val intent = Intent(
                this,
                OrderDetailActivity::class.java
            )
            intent.putExtra("merchantInfo", merchantInfo)
            intent.putExtra("orderInfo", orderInfo)
            startActivity(intent)
            finish()
        }

        if (arItemList.arItems.isNotEmpty()) {
            val layoutManager = GridLayoutManager(this, 24);
            val spanCounts = if (arItemList.arItems.size < 4) arItemList.arItems.size else 4
            layoutManager.spanSizeLookup = ARSpanSizeLookup(spanCounts)

            filterList.layoutManager = layoutManager
            filterList.addItemDecoration(ARFilterItemDecoration(spanCounts, 4, 18))
            filterList.adapter = ARFilterAdapter(this, arItemList.arItems)
            shareFilterLl.visibility = View.VISIBLE
        } else {
            shareFilterLl.visibility = View.GONE
        }



    }

    private fun payOrder(merchantInfo: ShopXMerchant, value: Int, nonce: String, orderId: String, loyaltyInfo: GetLoyaltyInfoResponse?) {
        // if the amount is 0, no need of payment
        if (value == 0) {
            plusPoint.visibility = View.GONE
            loadingView.visibility = View.GONE
            setMerchantLogoAndUsername(merchantInfo)
            return
        }
        val amountMoney = PaymentRequest.AmountMoney(value, "USD")
        val payRequest = PaymentRequest(UUID.randomUUID().toString(), nonce, amountMoney, orderId)
        SquareApiService.getInstance(merchantInfo.accessToken).payOrder(payRequest)
            .subscribe(object: Observer<PaymentResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: PaymentResponse?) {
                    runOnUiThread {
                        value?.let {
                            if (loyaltyInfo != null) {
                                accumulateTwoPoints(merchantInfo, loyaltyInfo, orderId)
                            } else {
                                plusPoint.visibility = View.GONE
                                loadingView.visibility = View.GONE
                                setMerchantLogoAndUsername(merchantInfo)
                            }
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@PaySuccessActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }




    private fun accumulateTwoPoints(merchantInfo: ShopXMerchant, loyaltyInfo: GetLoyaltyInfoResponse, orderId: String) {
        val accumulatePoints = AccumulateLoyaltyPointsRequest.AccumulatePoints(orderId)
        val request = AccumulateLoyaltyPointsRequest(UUID.randomUUID().toString(), merchantInfo.locationId, accumulatePoints)
        SquareApiService.getInstance(merchantInfo.accessToken).accumulatePoints(loyaltyInfo.loyaltyAccount, request)
            .subscribe(object: Observer<AccumulateLoyaltyPointsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AccumulateLoyaltyPointsResponse?) {
                    value?.let {
                        runOnUiThread {

                            plusPoint.text = "+${value.events?.get(0)?.accumulatePoints?.points.toString()} pts"
                            loadingView.visibility = View.GONE
                            setMerchantLogoAndUsername(merchantInfo)
                            EventBus.getDefault().post(
                                RefreshLoyaltyEvent(
                                    merchantInfo, value!!.events?.get(0)?.accumulatePoints?.points, loyaltyInfo.loyaltyAccount
                                )
                            )
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        plusPoint.visibility = View.GONE
                        loadingView.visibility = View.GONE
                        Toast.makeText(this@PaySuccessActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun setMerchantLogoAndUsername(merchantInfo: ShopXMerchant) {
        Glide.with(this@PaySuccessActivity).load(merchantInfo.logoUrl).into(merchantLogo)
        var nameString = if (PreferenceUtils.getUsername().length > 1) {
            PreferenceUtils.getUsername().substring(0, 2)
        } else {
            PreferenceUtils.getUsername().substring(0, 1)
        }
        nameString = nameString.toUpperCase()
        username.text = nameString
    }
}