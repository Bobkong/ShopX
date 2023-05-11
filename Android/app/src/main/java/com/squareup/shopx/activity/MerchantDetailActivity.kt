package com.squareup.shopx.activity

import android.content.Intent
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
import com.squareup.shopx.adapter.ItemListAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MerchantDetailActivity : AppCompatActivity() {

    private val TAG = "MerchantDetailActivity"
    private var itemList: RecyclerView? = null
    private lateinit var merchantInfo: ShopXMerchant
    private lateinit var cartInfo: TextView
    private var customerLoyaltyResponse: GetLoyaltyInfoResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_detail)

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

        merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant
        requestMerchantDetail(merchantInfo.accessToken)

        if (merchantInfo.ifLoyalty == 1) {
            requestLoyaltyInfo(merchantInfo.accessToken)
        }
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

                    if (value?.code == 1) {
                        Log.i(TAG, value.msg)
                        return@runOnUiThread
                    }

                    val loyaltyCL = findViewById<ConstraintLayout>(R.id.loyalty_cl)
                    val loyaltyPoints = findViewById<TextView>(R.id.loyal_points)
                    val enrollLoyalty = findViewById<TextView>(R.id.enroll_button)

                    if (value?.loyaltyInfo == null) {
                        loyaltyCL.visibility = View.GONE
                    } else {
                        loyaltyCL.visibility = View.VISIBLE

                        if (value.isEnrolled == 1) {
                            enrollLoyalty.visibility = View.GONE
                            loyaltyPoints.visibility = View.VISIBLE
                            loyaltyPoints.text = "${value.points} points"
                        } else {
                            enrollLoyalty.visibility = View.VISIBLE
                            loyaltyPoints.visibility = View.GONE

                            enrollLoyalty.setOnClickListener {
                                enrollLoyaltyProgram(accessToken, value.loyaltyInfo.program.id)
                            }
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

                        val loyaltyPoints = findViewById<TextView>(R.id.loyal_points)
                        val enrollLoyalty = findViewById<TextView>(R.id.enroll_button)

                        enrollLoyalty.visibility = View.GONE
                        loyaltyPoints.visibility = View.VISIBLE
                        loyaltyPoints.text = "0 points"

                        customerLoyaltyResponse?.isEnrolled = 1
                        customerLoyaltyResponse?.loyaltyAccount = value?.loyaltyAccount

                        Toast.makeText(this@MerchantDetailActivity, "Enroll Successfully!", Toast.LENGTH_SHORT).show()
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

                    runOnUiThread {
                        if (value?.code == 1) {
                            Toast.makeText(this@MerchantDetailActivity, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        value?.let {
                            itemList?.adapter = ItemListAdapter(it.items, this@MerchantDetailActivity, value, merchantInfo)
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