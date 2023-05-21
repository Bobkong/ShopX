package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.MerchantDetailAdapter
import com.squareup.shopx.adapter.RewardTiersAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.*
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MerchantDetailActivity : AppCompatActivity() {

    private val TAG = "MerchantDetailActivity"
    private var itemList: RecyclerView? = null
    private lateinit var merchantInfo: ShopXMerchant
    private lateinit var cartInfo: LinearLayout
    private lateinit var cartCount: TextView
    private lateinit var cartTotalPrice: TextView
    private var customerLoyaltyResponse: GetLoyaltyInfoResponse? = null
    private var merchantItemsResponse: GetMerchantDetailResponse? = null
    private lateinit var backButton: ImageView
    private lateinit var loadingView: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_detail)

        loadingView = findViewById(R.id.loading_view)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant

        cartInfo = findViewById(R.id.cart_info)
        cartInfo.setOnClickListener {
            val cartBottomDialog = CartBottomDialog(this, R.style.BottomSheetDialogStyle)
            cartBottomDialog.init(this, merchantInfo)
        }

        cartTotalPrice = findViewById(R.id.total_cart_price)
        cartCount = findViewById(R.id.cart_total_count)
//        cartInfo.setOnClickListener {
//            val intent = Intent(this@MerchantDetailActivity, OrderActivity::class.java)
//            intent.putExtra("merchant", merchantInfo)
//
//            if (merchantInfo.ifLoyalty == 1 && customerLoyaltyResponse != null && customerLoyaltyResponse!!.isEnrolled == 1) {
//                intent.putExtra("loyalty", customerLoyaltyResponse)
//            }
//            startActivity(intent)
//        }

        itemList = findViewById(R.id.item_list)
        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        setViewCart()
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
        if (event.merchant.equals(merchantInfo)) {
            setViewCart()
        }
    }

    fun setViewCart() {
        if (AllMerchants.getTotalItemCount(merchantInfo) == 0) {
            cartInfo.visibility = View.GONE
        } else {
            cartInfo.visibility = View.VISIBLE
            cartCount.text = AllMerchants.getTotalItemCount(merchantInfo).toString()
            cartTotalPrice.text = "$ " + String.format(
                "%.2f",
                AllMerchants.getPrice(merchantInfo) / 100.0
            )
        }
    }

    fun showLoyaltyBottomSheet() {
        customerLoyaltyResponse?.let {
            val bottomSheetDialog = BottomSheetDialog(this, R.style.LoyaltyBottomSheetDialogStyle)
            val dialogView = layoutInflater.inflate(R.layout.merchant_loyalty_bottom_sheet, null, false)
            bottomSheetDialog.setContentView(dialogView)

            try {
                // hack bg color of the BottomSheetDialog
                val parent = dialogView!!.parent as ViewGroup
                parent.setBackgroundResource(android.R.color.transparent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val loyaltyCard = dialogView.findViewById<RadiusCardView>(R.id.loyalty_card)
            val loyaltyHeader = dialogView.findViewById<TextView>(R.id.loyalty_header)
            val merchantLogo = dialogView.findViewById<RoundRectImageView>(R.id.merchant_logo)
            val username = dialogView.findViewById<TextView>(R.id.user_name)
            val merchantName = dialogView.findViewById<TextView>(R.id.merchant_name)
            val userPoints = dialogView.findViewById<TextView>(R.id.user_points)
            val earningRules = dialogView.findViewById<TextView>(R.id.earning_rules_text)
            val rewardTiersList = dialogView.findViewById<RecyclerView>(R.id.reward_tiers)
            val enrollNow = dialogView.findViewById<TextView>(R.id.enroll_now)

            if (it.isEnrolled == 1) {
                loyaltyCard.visibility = View.VISIBLE
                Glide.with(this).load(merchantInfo.logoUrl).into(merchantLogo)
                var nameString = if (PreferenceUtils.getUsername().length > 1) {
                    PreferenceUtils.getUsername().substring(0, 2)
                } else {
                    PreferenceUtils.getUsername().substring(0, 1)
                }
                nameString = nameString.toUpperCase()
                username.text = nameString

                userPoints.text = "${it.points} pts"
                merchantName.text = merchantInfo.businessName
                loyaltyHeader.text = "Loyalty Details"
            } else {
                loyaltyCard.visibility = View.GONE
                loyaltyHeader.text = "Enroll Loyalty"
            }

            var accrualRuleString = ""
            for (i in it.loyaltyInfo.program.accrualRules.indices) {
                val accrualRule = it.loyaltyInfo.program.accrualRules[i]
                accrualRuleString += (i+1).toString() + ". Earn " + accrualRule.points + " points"
                if (accrualRule.accrualType == "SPEND") {
                    accrualRuleString += " for every $" + accrualRule.spendData.amountMoney.amount / 100.0 + " spent in a single transaction."
                } else if (accrualRule.accrualType == "VISIT") {
                    accrualRuleString += " for every visit ($" + accrualRule.visitData.minimumAmountMoney.amount / 100.0 + " minimum purchase)."
                }
                if (i != it.loyaltyInfo.program.accrualRules.size - 1){
                    accrualRuleString += "\n"
                }
            }
            earningRules.text = accrualRuleString

            val layoutManager = GridLayoutManager(this, 2)
            rewardTiersList.layoutManager = layoutManager
            rewardTiersList.adapter = RewardTiersAdapter(this, it)

            enrollNow.setOnClickListener {
                enrollLoyaltyProgram(merchantInfo.accessToken, customerLoyaltyResponse!!.loyaltyInfo.program.id)
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    fun showItemBottomSheet(item: GetMerchantDetailResponse.Item) {
        val bottomSheetDialog = ItemBottomDialog(this, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.init(this, ItemBottomDialog.FROM_MERCHANT_DETAIL, item, merchantInfo)
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
                    loadingView.visibility = View.GONE
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
                    loadingView.visibility = View.GONE
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

                        // go to enroll success activity

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
                            loadingView.visibility = View.GONE
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
                                loadingView.visibility = View.GONE
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
                        loadingView.visibility = View.GONE
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