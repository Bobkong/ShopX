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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.CartItemListAdapter
import com.squareup.shopx.adapter.OrderDetailItemAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.RadiusCardView
import com.squareup.shopx.widget.RoundRectImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.util.*

class OrderDetailActivity : AppCompatActivity() {
    private val TAG = "OrderDetailActivity"

    private var itemList: RecyclerView? = null
    private lateinit var subtotalPrice: TextView
    private lateinit var discountValueTv: TextView
    private lateinit var totalPrice: TextView
    private lateinit var discountValueCl: ConstraintLayout
    private lateinit var loyaltyCl: ConstraintLayout
    private lateinit var loyaltyValueTv: TextView
    private var orderInfo: PlaceOrderRequest? = null
    private lateinit var loadingView: ConstraintLayout
    private lateinit var reorder: TextView
    private lateinit var getHelp: TextView
    private lateinit var back: ImageView
    private var merchantInfo: ShopXMerchant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this, true)
        merchantInfo = intent?.extras?.getSerializable("merchantInfo") as ShopXMerchant
        orderInfo = intent?.extras?.getSerializable("orderInfo") as PlaceOrderRequest

        back = findViewById(R.id.back)
        itemList = findViewById(R.id.order_item_list)
        subtotalPrice = findViewById(R.id.subtotal_price)
        discountValueTv = findViewById(R.id.discount_value)
        totalPrice = findViewById(R.id.total_value)
        discountValueCl = findViewById(R.id.discount_cl)
        loadingView = findViewById(R.id.loading_view)
        loyaltyCl = findViewById(R.id.loyalty_cl)
        loyaltyValueTv = findViewById(R.id.loyalty_value)
        reorder = findViewById(R.id.reorder)
        getHelp = findViewById(R.id.get_help)
        reorder.setOnClickListener {
            val intent = Intent(
                this,
                MerchantDetailActivity::class.java
            )
            intent.putExtra("merchant", merchantInfo)
            startActivity(intent)
        }

        back.setOnClickListener {
            finish()
        }
        setPrice()

        requestOrderItems()

    }

    private fun requestOrderItems() {
        if (orderInfo == null && merchantInfo == null) {
            return
        }
        ShopXApiService.getInstance().getOrderItems(orderInfo!!.orderId, merchantInfo!!.accessToken)
            .subscribe(object : Observer<GetOrderItemsResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: GetOrderItemsResponse?) {
                    value?.let {
                        runOnUiThread {
                            loadingView.visibility = View.GONE
                            itemList?.layoutManager = LinearLayoutManager(this@OrderDetailActivity, RecyclerView.VERTICAL, false)
                            itemList?.adapter = OrderDetailItemAdapter(this@OrderDetailActivity, value.items, merchantInfo)
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderDetailActivity, e?.message, Toast.LENGTH_SHORT).show()
                        loadingView.visibility = View.GONE
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun setPrice() {
        orderInfo?.let {
            subtotalPrice.text = "$ " + String.format(
                "%.2f",
                it.originalPrice / 100.0
            )

            if (it.discountValue == 0) {
                discountValueCl.visibility = View.GONE
            } else {
                discountValueCl.visibility = View.VISIBLE
                discountValueTv.text = "- $ " + String.format(
                    "%.2f",
                    it.discountValue / 100.0
                )
            }

            if (it.loyaltyValue == 0) {
                loyaltyCl.visibility = View.GONE
            } else {
                loyaltyCl.visibility = View.VISIBLE
                loyaltyValueTv.text = "- $ " + String.format(
                    "%.2f",
                    it.loyaltyValue / 100.0
                )
            }

            totalPrice.text = "$ " + String.format(
                "%.2f",
                it.finalPrice / 100.0
            )
        }

    }


}