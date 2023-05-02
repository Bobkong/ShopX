package com.squareup.shopx.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.ItemListAdapter
import com.squareup.shopx.model.GetMerchantDetailResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MerchantDetailActivity : AppCompatActivity() {

    private var itemList: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_detail)

        itemList = findViewById(R.id.item_list)
        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        requestMerchantDetail(intent.extras?.getString("accessToken"))

    }

    private fun requestMerchantDetail(accessToken: String?) {
        ShopXApiService.getInstance().getMerchantDetail(accessToken, "")
            .subscribe(object: Observer<GetMerchantDetailResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GetMerchantDetailResponse?) {

                    runOnUiThread {
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