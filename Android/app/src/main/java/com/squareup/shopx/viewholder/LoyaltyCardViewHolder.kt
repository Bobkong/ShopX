package com.squareup.shopx.viewholder

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.shopx.R
import com.squareup.shopx.activity.LoyaltyCardsFragment
import com.squareup.shopx.activity.MerchantDetailActivity
import com.squareup.shopx.adapter.RewardTiersAdapter
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.GetLoyaltyInfoResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.widget.RoundRectImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class LoyaltyCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setData(fragment: LoyaltyCardsFragment, merchantInfo: ShopXMerchant) {
        val loadingView = itemView.findViewById<ConstraintLayout>(R.id.loading_view)
        ShopXApiService.getInstance().getLoyaltyinfo(merchantInfo.accessToken, PreferenceUtils.getUserPhone())
            .subscribe(object: Observer<GetLoyaltyInfoResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: GetLoyaltyInfoResponse?) {
                    fragment.requireActivity().runOnUiThread {
                        loadingView.visibility = View.GONE
                        setLoyaltyInfo(value!!, fragment.requireActivity(), merchantInfo)
                    }
                }

                override fun onError(e: Throwable?) {
                    fragment.requireActivity().runOnUiThread {
                        loadingView.visibility = View.GONE
                        Toast.makeText(fragment.requireActivity(), e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })

    }

    private fun setLoyaltyInfo(loyaltyInfo: GetLoyaltyInfoResponse, activity: Activity, merchantInfo: ShopXMerchant) {
        val merchantLogo = itemView.findViewById<RoundRectImageView>(R.id.merchant_logo)
        val username = itemView.findViewById<TextView>(R.id.user_name)
        val merchantName = itemView.findViewById<TextView>(R.id.merchant_name)
        val userPoints = itemView.findViewById<TextView>(R.id.user_points)
        val earningRules = itemView.findViewById<TextView>(R.id.earning_rules_text)
        val rewardTiersList = itemView.findViewById<RecyclerView>(R.id.reward_tiers)


        Glide.with(activity).load(merchantInfo.logoUrl).into(merchantLogo)
        var nameString = if (PreferenceUtils.getUsername().length > 1) {
            PreferenceUtils.getUsername().substring(0, 2)
        } else {
            PreferenceUtils.getUsername().substring(0, 1)
        }
        nameString = nameString.toUpperCase()
        username.text = nameString

        userPoints.text = "${loyaltyInfo.points} pts"
        merchantName.text = merchantInfo.businessName

        var accrualRuleString = ""
        for (i in loyaltyInfo.loyaltyInfo.program.accrualRules.indices) {
            val accrualRule = loyaltyInfo.loyaltyInfo.program.accrualRules[i]
            accrualRuleString += (if (loyaltyInfo.loyaltyInfo.program.accrualRules.size == 1) "" else (i+1).toString() + ". ") + "Earn " + accrualRule.points + " points"
            if (accrualRule.accrualType == "SPEND") {
                accrualRuleString += " for every $" + accrualRule.spendData.amountMoney.amount / 100.0 + " spent in a single transaction."
            } else if (accrualRule.accrualType == "VISIT") {
                accrualRuleString += " for every visit ($" + accrualRule.visitData.minimumAmountMoney.amount / 100.0 + " minimum purchase)."
            }
            if (i != loyaltyInfo.loyaltyInfo.program.accrualRules.size - 1){
                accrualRuleString += "\n"
            }
        }
        earningRules.text = accrualRuleString

        val layoutManager = GridLayoutManager(activity, 2)
        rewardTiersList.layoutManager = layoutManager
        rewardTiersList.adapter = RewardTiersAdapter(activity, loyaltyInfo, RewardTiersAdapter.FROM_MERCHANT_DETAIL)

        itemView.findViewById<ImageView>(R.id.view_merchant).setOnClickListener { view: View? ->
            val intent = Intent(activity, MerchantDetailActivity::class.java)
            intent.putExtra("merchant", merchantInfo)
            activity.startActivity(intent)
        }
    }
}