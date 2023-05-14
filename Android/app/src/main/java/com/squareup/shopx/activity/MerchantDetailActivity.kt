package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.MerchantDetailAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.Transparent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MerchantDetailActivity : AppCompatActivity() {

    private val TAG = "MerchantDetailActivity"
    private var itemList: RecyclerView? = null
    private lateinit var merchantInfo: ShopXMerchant
    private lateinit var merchantLogo: ImageView
    private lateinit var cartInfo: TextView
    private var customerLoyaltyResponse: GetLoyaltyInfoResponse? = null
    private var merchantItemsResponse: GetMerchantDetailResponse? = null
    private lateinit var backButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_detail)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant

        cartInfo = findViewById(R.id.cart_info)
        cartInfo.setOnClickListener {
            val intent = Intent(this@MerchantDetailActivity, OrderActivity::class.java)
            intent.putExtra("merchant", merchantInfo)

            if (merchantInfo.ifLoyalty == 1 && customerLoyaltyResponse != null && customerLoyaltyResponse!!.isEnrolled == 1) {
                intent.putExtra("loyalty", customerLoyaltyResponse)
            }
            startActivity(intent)
        }

        itemList = findViewById(R.id.item_list)
        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        requestMerchantDetail(merchantInfo.accessToken)


    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdateEvent(event: CartUpdateEvent) {
        if (event.accessToken == merchantInfo.accessToken) {
            cartInfo.text = AllMerchants.getPrice(merchantInfo).toString()
        }
    }

    private fun requestLoyaltyInfo(accessToken: String?) {
        // todo: change the test code
        ShopXApiService.getInstance().getLoyaltyinfo(accessToken, "+18583190000")
            .subscribe(object: Observer<GetLoyaltyInfoResponse> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onNext(value: GetLoyaltyInfoResponse?) {
                customerLoyaltyResponse = value
                runOnUiThread {
                    itemList?.adapter = MerchantDetailAdapter(
                        this@MerchantDetailActivity,
                        merchantItemsResponse,
                        merchantInfo,
                        customerLoyaltyResponse
                    )
                }
            }

            override fun onError(e: Throwable?) {
                runOnUiThread {
                    Toast.makeText(this@MerchantDetailActivity, e?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onComplete() {
            }

        })
    }

    private fun enrollLoyaltyProgram(accessToken: String?, programId: String?) {
        // todo: change the test code
        ShopXApiService.getInstance().enrollLoyalty(accessToken, "+18583190000", programId)
            .subscribe(object: Observer<EnrollLoyaltyResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: EnrollLoyaltyResponse?) {
                    runOnUiThread {

                        if (value?.code == 1) {
                            Toast.makeText(this@MerchantDetailActivity, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                     }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@MerchantDetailActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun requestMerchantDetail(accessToken: String?) {
        ShopXApiService.getInstance().getMerchantDetail(accessToken, "")
            .subscribe(object: Observer<GetMerchantDetailResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GetMerchantDetailResponse?) {
                    merchantItemsResponse = value
                    runOnUiThread {
                        if (value?.code == 1) {
                            Toast.makeText(this@MerchantDetailActivity, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        value?.let {
                            if (merchantInfo.ifLoyalty == 1) {
                                requestLoyaltyInfo(merchantInfo.accessToken)
                            } else {
                                itemList?.adapter = MerchantDetailAdapter(
                                    this@MerchantDetailActivity,
                                    value,
                                    merchantInfo,
                                    null
                                )
                            }

                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@MerchantDetailActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })

    }
}