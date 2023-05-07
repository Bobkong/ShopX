package com.squareup.shopx.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.squareup.shopx.R
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse
import com.squareup.shopx.model.LoyaltyProgramResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.PreferenceUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class LoyaltyActivity : AppCompatActivity() {
    private val TAG = "LoyaltyActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loyalty_program)

        ShopXApiService.getInstance().getAllLoyaltyRecordsResponse(PreferenceUtils.getUserPhone())
            .subscribe(object : Observer<GetAllLoyaltyRecordsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GetAllLoyaltyRecordsResponse?) {
                    value?.loyaltyMerchants?.size?.toString()?.let { Log.i(TAG, it) }
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