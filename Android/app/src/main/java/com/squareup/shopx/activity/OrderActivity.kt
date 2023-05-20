package com.squareup.shopx.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.OrderItemListAdapter
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.CreateLoyaltyRewardRequest
import com.squareup.shopx.model.CreateLoyaltyRewardRequest.Reward
import com.squareup.shopx.model.CreateLoyaltyRewardResponse
import com.squareup.shopx.model.CreateOrderRequest
import com.squareup.shopx.model.CreateOrderRequest.LineItem
import com.squareup.shopx.model.CreateOrderRequest.Order
import com.squareup.shopx.model.CreateOrderResponse
import com.squareup.shopx.model.GetLoyaltyInfoResponse
import com.squareup.shopx.model.LoyaltyProgramResponse
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.UUID

class OrderActivity: AppCompatActivity() {
    private val TAG = "OrderActivity"
    private var itemList: RecyclerView? = null
    private lateinit var merchantInfo: ShopXMerchant
    private var loyaltyInfo: GetLoyaltyInfoResponse? = null
    private lateinit var payOrder: TextView
    private lateinit var originalPrice: TextView
    private lateinit var discountPrice: TextView
    private lateinit var finalPrice: TextView
    private lateinit var loyaltyPoints: TextView
    private lateinit var createRewards: TextView
    private lateinit var loyaltyCl: ConstraintLayout
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant
        loyaltyInfo = intent.extras?.getSerializable("loyalty") as? GetLoyaltyInfoResponse

        itemList = findViewById(R.id.order_item_list)
        payOrder = findViewById(R.id.pay_order)
        originalPrice = findViewById(R.id.original_price)
        discountPrice = findViewById(R.id.discount_price)
        finalPrice = findViewById(R.id.final_price)
        loyaltyPoints = findViewById(R.id.loyal_points)
        createRewards = findViewById(R.id.create_rewards)
        loyaltyCl = findViewById(R.id.loyalty_cl)

        loyaltyInfo?.let {
            loyaltyCl.visibility = View.VISIBLE
            loyaltyPoints.text = it.points.toString()
            createRewards.setOnClickListener {
                createRewardForOrder()
            }
        }

//
//        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//
//        itemList?.adapter = OrderItemListAdapter(AllMerchants.getCartItems(merchantInfo), this@OrderActivity, merchantInfo)
//
//        val lineItems = ArrayList<LineItem>()
//        for (item in AllMerchants.getCartItems(merchantInfo)) {
//            val lineItem = LineItem(item.itemVariationId)
//            lineItems.add(lineItem)
//        }
//        val order = Order(merchantInfo.locationId, lineItems, CreateOrderRequest.PricingOptions(true))
//
//        val orderRequest = CreateOrderRequest(UUID.randomUUID().toString(), order)
//        SquareApiService.getInstance(merchantInfo.accessToken).createOrder(orderRequest)
//            .subscribe(object: Observer<CreateOrderResponse> {
//                override fun onSubscribe(d: Disposable?) {
//
//                }
//
//                override fun onNext(value: CreateOrderResponse?) {
//                    runOnUiThread {
//                        value?.let {
//                            Log.i(TAG, it.order.id)
//                            orderId = it.order.id
//                            originalPrice.text = (it.order.totalMoney.amount + it.order.discountAmount.amount).toString()
//                            discountPrice.text = it.order.discountAmount.amount.toString()
//                            finalPrice.text = it.order.totalMoney.amount.toString()
//                        }
//                    }
//
//                }
//
//                override fun onError(e: Throwable?) {
//                    runOnUiThread {
//                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onComplete() {
//                }
//
//            })

    }

    fun createRewardForOrder() {
        if (orderId.isNullOrEmpty()) {
            Toast.makeText(this@OrderActivity, "Order hasn't been created, please try later", Toast.LENGTH_SHORT).show()
            return
        }
        val reward = Reward(loyaltyInfo?.loyaltyAccount, loyaltyInfo?.loyaltyInfo?.program?.rewardTiers?.get(0)?.id, orderId)
        val createLoyaltyRewardRequest = CreateLoyaltyRewardRequest(UUID.randomUUID().toString(), reward)
        SquareApiService.getInstance(merchantInfo.accessToken).createLoyaltyReward(createLoyaltyRewardRequest)
            .subscribe(object: Observer<CreateLoyaltyRewardResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: CreateLoyaltyRewardResponse?) {
                    value?.let {
                        retrieveOrder()
                        runOnUiThread {
                            Toast.makeText(this@OrderActivity, "Create reward successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    fun retrieveOrder() {
        if (orderId.isNullOrEmpty()) {
            Toast.makeText(this@OrderActivity, "Order hasn't been created, please try later", Toast.LENGTH_SHORT).show()
            return
        }
        SquareApiService.getInstance(merchantInfo.accessToken).retrieveOrder(orderId)
            .subscribe(object : Observer<CreateOrderResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: CreateOrderResponse?) {
                    runOnUiThread {
                        value?.let {
                            Log.i(TAG, it.order.id)
                            orderId = it.order.id
                            originalPrice.text = (it.order.totalMoney.amount + it.order.discountAmount.amount).toString()
                            discountPrice.text = it.order.discountAmount.amount.toString()
                            finalPrice.text = it.order.totalMoney.amount.toString()
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }
}