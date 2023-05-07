package com.squareup.shopx.activity

import android.os.Bundle
import android.os.PersistableBundle
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
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.model.GetLoyaltyInfoResponse
import com.squareup.shopx.model.GetMerchantDetailResponse
import com.squareup.shopx.model.LoyaltyProgramResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MerchantDetailActivity : AppCompatActivity() {

    private val TAG = "MerchantDetailActivity"
    private var itemList: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_detail)

        itemList = findViewById(R.id.item_list)
        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        requestMerchantDetail(intent.extras?.getString("accessToken"))

        if (intent.extras?.getInt("ifLoyalty") == 1) {
            requestLoyaltyInfo(intent.extras?.getString("accessToken"))
        }
    }

    private fun requestLoyaltyInfo(accessToken: String?) {
        ShopXApiService.getInstance().getLoyaltyinfo(accessToken, PreferenceUtils.getUserPhone())
            .subscribe(object: Observer<GetLoyaltyInfoResponse> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onNext(value: GetLoyaltyInfoResponse?) {
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
        ShopXApiService.getInstance().enrollLoyalty(accessToken, PreferenceUtils.getUserPhone(), programId)
            .subscribe(object: Observer<GeneralResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GeneralResponse?) {
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
                            itemList?.adapter = ItemListAdapter(it.items, this@MerchantDetailActivity, value)
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