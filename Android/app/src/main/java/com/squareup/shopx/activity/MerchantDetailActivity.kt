package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.MerchantDetailAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.CustomDialog
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

    fun showItemBottomSheet(item: GetMerchantDetailResponse.Item) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.merchant_item_bottom_sheet, null, false)
        bottomSheetDialog.setContentView(dialogView)

        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView!!.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val itemImage = dialogView.findViewById<ImageView>(R.id.item_image)
        val itemName = dialogView.findViewById<TextView>(R.id.item_name)
        val itemDesc = dialogView.findViewById<TextView>(R.id.item_desc)
        Glide.with(this)
            .load(item.itemImage)
            .into(itemImage)
        itemName.text = item.itemName
        if (item.itemDescription.isNullOrEmpty()) {
            itemDesc.visibility = View.GONE
        } else {
            itemDesc.text = item.itemDescription
            itemDesc.visibility = View.VISIBLE
        }
        bottomSheetDialog.show()
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

                            for (item in it.items) {
                                if (item.pricingType != "FIXED_PRICING") {
                                    showBookingDialog()
                                    break
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

    private fun showBookingDialog() {
        val customDialog = CustomDialog(this, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        leftActivity.visibility = View.GONE
        rightAction.text = "OK"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "Please note that only booking is currently available for this service. Kindly visit the store in-person to complete your transaction. Thank you!"
        customDialog.show()

        rightAction.setOnClickListener {
            customDialog.dismiss()
        }
    }
}