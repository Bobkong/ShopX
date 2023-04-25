package com.squareup.easyshop.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.squareup.easyshop.R
import com.squareup.easyshop.model.LoyaltyProgramResponse
import com.squareup.easyshop.netservice.SquareAPI.SquareApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class LoyaltyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loyalty_program)

        val merchantToken = intent.extras?.getString("merchantToken")

        SquareApiService.getInstance(merchantToken).retrieveLoyaltyProgram()
            .subscribe(object : Observer<LoyaltyProgramResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(response: LoyaltyProgramResponse) {
                    Log.i("loyalty", "earning type: " + response.program.accrualRules?.get(0)?.accrualType)

                    var accrualRuleString = ""
                    var rewardTiresString = ""
                    runOnUiThread {
                        // generate accrual rule string
                        response.program.accrualRules?.get(0)?.let { accrualRule ->
                            accrualRuleString += "Earn " + accrualRule.points + " Points"
                            if (accrualRule.accrualType == "SPEND") {
                                accrualRuleString += " for every $" + accrualRule.spendData.amountMoney.amount / 100.0 + " spent in a single transaction."
                            } else if (accrualRule.accrualType == "VISIT") {
                                accrualRuleString += " for every visit. $" + accrualRule.visitData.minimumAmountMoney.amount / 100.0 + " minimum purchase."
                            }
                        }
                        findViewById<TextView>(R.id.accrual_rule).text = accrualRuleString

                        // generate reward tiers string
                        for (tier in response.program.rewardTiers) {
                            rewardTiresString += "Redeem " + tier.points + " Points to get " + tier.name + "\n"
                        }

                        findViewById<TextView>(R.id.reward_tiers ).text = rewardTiresString
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@LoyaltyActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })

    }
}