package com.squareup.shopx.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.shopx.R
import com.squareup.shopx.adapter.RewardTiersAdapter
import com.squareup.shopx.model.*
import com.squareup.shopx.model.LoyaltyProgramResponse.RewardTier
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.CustomDialog
import com.squareup.shopx.widget.RoundRectImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class RedeemActivity: AppCompatActivity(){

    val TAG = "RedeemActivity"
    private lateinit var back: ImageView
    private var orderId: String? = null
    private var merchantInfo: AllMerchantsResponse.ShopXMerchant? = null
    private var loyaltyInfo: GetLoyaltyInfoResponse? = null
    private lateinit var userPoints: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem)

        merchantInfo = intent.extras?.getSerializable("merchant") as AllMerchantsResponse.ShopXMerchant
        loyaltyInfo = intent.extras?.getSerializable("loyalty") as? GetLoyaltyInfoResponse
        orderId = intent.extras?.getString("orderId")

        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }
        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this, true)
        val merchantLogo = findViewById<RoundRectImageView>(R.id.merchant_logo)
        val username = findViewById<TextView>(R.id.user_name)
        val merchantName = findViewById<TextView>(R.id.merchant_name)
        userPoints = findViewById(R.id.user_points)
        val rewardTiersList = findViewById<RecyclerView>(R.id.reward_tiers)

        loyaltyInfo?.let {
            Glide.with(this).load(merchantInfo!!.logoUrl).into(merchantLogo)
            var nameString = if (PreferenceUtils.getUsername().length > 1) {
                PreferenceUtils.getUsername().substring(0, 2)
            } else {
                PreferenceUtils.getUsername().substring(0, 1)
            }
            nameString = nameString.toUpperCase()
            username.text = nameString

            userPoints.text = "${it.points} pts"
            merchantName.text = merchantInfo!!.businessName

            val layoutManager = GridLayoutManager(this, 2)
            rewardTiersList.layoutManager = layoutManager
            rewardTiersList.adapter = RewardTiersAdapter(this, it, RewardTiersAdapter.FROM_ORDER)
        }

        EventBus.getDefault().register(this);

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedeemRewardEvent(event: RedeemRewardEvent) {
        loyaltyInfo?.loyaltyInfo?.program?.rewardTiers?.let {
            for (rewardTier in it) {
                if (rewardTier.id == event.rewardTierId) {
                    showConfirmRedeemDialog(rewardTier)
                }
            }
        }

    }

    private fun showConfirmRedeemDialog(rewardTier: RewardTier){
        val customDialog = CustomDialog(this, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        rightAction.text = "Yes"
        leftActivity.text = "Cancel"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "Are you sure you want to redeem this reward? This reward will consume " + rewardTier.points.toString() + " points."
        customDialog.show()

        leftActivity.setOnClickListener {
            customDialog.dismiss()
        }

        rightAction.setOnClickListener {
            createRewardForOrder(rewardTier)
            customDialog.dismiss()
        }
    }

    private fun showPointsInsufficientDialog(){
        val customDialog = CustomDialog(this, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        leftActivity.visibility = View.GONE
        rightAction.text = "OK"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "You have not met the redemption point requirements, please try again after getting more points."
        customDialog.show()

        rightAction.setOnClickListener {
            customDialog.dismiss()
        }
    }

    private fun showSuccessDialog(){
        val customDialog = CustomDialog(this, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        leftActivity.visibility = View.GONE
        rightAction.text = "Got it"
        dialogTitle.text = "Congratulations!"
        dialogDesc.text = "You have successfully redeemed the reward. Enjoy the benefits and thank you for being a loyal customer!"
        customDialog.show()

        rightAction.setOnClickListener {
            customDialog.dismiss()
        }
    }


    private fun createRewardForOrder(rewardTier: RewardTier) {
        if (orderId.isNullOrEmpty()) {
            Toast.makeText(this@RedeemActivity, "Order hasn't been created, please try later", Toast.LENGTH_SHORT).show()
            return
        }

        if (loyaltyInfo!!.points < rewardTier.points) {
            showPointsInsufficientDialog()
            return
        }

        val reward = CreateLoyaltyRewardRequest.Reward(
            loyaltyInfo?.loyaltyAccount,
            rewardTier.id,
            orderId
        )
        val createLoyaltyRewardRequest = CreateLoyaltyRewardRequest(UUID.randomUUID().toString(), reward)
        SquareApiService.getInstance(merchantInfo!!.accessToken).createLoyaltyReward(createLoyaltyRewardRequest)
            .subscribe(object: Observer<CreateLoyaltyRewardResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: CreateLoyaltyRewardResponse?) {
                    value?.let {
                        runOnUiThread {
                            showSuccessDialog()
                            EventBus.getDefault().post(RedeemSuccessEvent(orderId))
                            EventBus.getDefault().post(RefreshLoyaltyEvent(merchantInfo, -1 * rewardTier.points))
                            loyaltyInfo?.let {
                                it.points -= rewardTier.points
                                userPoints.text = "${it.points} pts"
                            }
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@RedeemActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }


}