package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.CartItemListAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.CreateOrderRequest.BasePriceMoney
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.RadiusCardView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.UUID

class OrderActivity: AppCompatActivity(), CartCallback {
    private val TAG = "OrderActivity"
    private var itemList: RecyclerView? = null
    private lateinit var merchantInfo: ShopXMerchant
    private var loyaltyInfo: GetLoyaltyInfoResponse? = null
    private lateinit var gettingRewardsLayout: RadiusCardView
    private lateinit var subtotalPrice: TextView
    private lateinit var discountValueTv: TextView
    private lateinit var totalPrice: TextView
    private lateinit var placeOrder: TextView
    private lateinit var discountValueCl: ConstraintLayout
    private lateinit var loyaltyCl: ConstraintLayout
    private lateinit var loyaltyValueTv: TextView
    private var orderId: String? = null
    private lateinit var loadingView: ConstraintLayout
    private lateinit var back: ImageView
    private var loyaltyValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)

        merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant
        loyaltyInfo = intent.extras?.getSerializable("loyalty") as? GetLoyaltyInfoResponse

        itemList = findViewById(R.id.order_item_list)
        gettingRewardsLayout = findViewById(R.id.redeem_rewards)
        subtotalPrice = findViewById(R.id.subtotal_price)
        discountValueTv = findViewById(R.id.discount_value)
        totalPrice = findViewById(R.id.total_value)
        placeOrder = findViewById(R.id.place_order)
        discountValueCl = findViewById(R.id.discount_cl)
        loadingView = findViewById(R.id.loading_view)
        loyaltyCl = findViewById(R.id.loyalty_cl)
        loyaltyValueTv = findViewById(R.id.loyalty_value)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            finish()
        }

        loyaltyInfo?.let {
            gettingRewardsLayout.visibility = View.VISIBLE
        }

        gettingRewardsLayout.setOnClickListener {
            orderId?.let {
                val intent = Intent(this@OrderActivity, RedeemActivity::class.java)
                intent.putExtra("orderId", it)
                intent.putExtra("merchant", merchantInfo)
                intent.putExtra("loyalty", loyaltyInfo)
                startActivity(intent)
            }
        }

        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        itemList?.adapter = CartItemListAdapter(this, merchantInfo, this, CartItemListAdapter.FROM_ORDER)

        updatePrice()

        val lineItems = ArrayList<CreateOrderRequest.LineItem>()
        for (item in AllMerchants.getCartItems(merchantInfo).keys) {
            val basePriceMoney = BasePriceMoney(item.getItemDiscountPrice(merchantInfo).toInt())
            val lineItem = CreateOrderRequest.LineItem(basePriceMoney, AllMerchants.getCountOfAnItem(merchantInfo, item).toString(), item.itemName, item.itemVariationId)
            lineItems.add(lineItem)
        }
        val order = CreateOrderRequest.Order(merchantInfo.locationId, lineItems, CreateOrderRequest.PricingOptions())

        val orderRequest = CreateOrderRequest(UUID.randomUUID().toString(), order)
        SquareApiService.getInstance(merchantInfo.accessToken).createOrder(orderRequest)
            .subscribe(object: Observer<CreateOrderResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: CreateOrderResponse?) {
                    runOnUiThread {
                        value?.let {
                            Log.i(TAG, it.order.id)
                            orderId = it.order.id
                        }
                        loadingView.visibility = View.GONE
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
                        loadingView.visibility = View.GONE
                    }
                }

                override fun onComplete() {
                }

            })
        EventBus.getDefault().register(this);

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedeemSuccessEvent(event: RedeemSuccessEvent) {
        if (event.orderId == orderId) {
            retrieveOrder()
            loadingView.visibility = View.VISIBLE
        }
    }



    private fun retrieveOrder() {
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
                            loadingView.visibility = View.GONE
                            loyaltyValue = it.order.discountAmount.amount
                            updatePrice()
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
                        loadingView.visibility = View.GONE
                    }
                }

                override fun onComplete() {
                }

            })
    }

    override fun dismissCart() {
        finish()
    }

    override fun updatePrice() {
        subtotalPrice.text = "$ " + String.format(
            "%.2f",
            AllMerchants.getOriginalPrice(merchantInfo) / 100.0
        )

        val discountValue = AllMerchants.getOriginalPrice(merchantInfo) - AllMerchants.getPrice(merchantInfo)
        if (discountValue == 0F) {
            discountValueCl.visibility = View.GONE
        } else {
            discountValueCl.visibility = View.VISIBLE
            discountValueTv.text = "- $ " + String.format(
                "%.2f",
                discountValue / 100.0
            )
        }

        if (loyaltyValue == 0) {
            loyaltyCl.visibility = View.GONE
        } else {
            loyaltyCl.visibility = View.VISIBLE
            loyaltyValueTv.text = "- $ " + String.format(
                "%.2f",
                loyaltyValue / 100.0
            )
        }

        totalPrice.text = "$ " + String.format(
            "%.2f",
            (AllMerchants.getPrice(merchantInfo) - loyaltyValue) / 100.0
        )
    }
}