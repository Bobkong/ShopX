package com.squareup.easyshop.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.squareup.easyshop.R
import com.squareup.easyshop.model.LocationResponse
import com.squareup.easyshop.model.MerchantResponse
import com.squareup.easyshop.netservice.SquareAPI.SquareApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class RetrieveMerchantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_merchant)

        val merchantId = intent.extras?.getString("merchantId")
        val merchantToken = intent.extras?.getString("merchantToken")

        SquareApiService.getInstance(merchantToken).retrieveMerchant(merchantId)
            .subscribe(object : Observer<MerchantResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(response: MerchantResponse) {
                    runOnUiThread {
                        findViewById<TextView>(R.id.business_name).text = response?.merchant?.businessName ?: ""
                    }

                    SquareApiService.getInstance(merchantToken).retrieveLocation(response?.merchant?.mainLocationId)
                        .subscribe(object : Observer<LocationResponse> {
                            override fun onSubscribe(d: Disposable?) {
                            }

                            override fun onNext(locationResponse: LocationResponse) {
                                runOnUiThread {
                                    Glide.with(this@RetrieveMerchantActivity)
                                        .load(locationResponse.location.logoUrl)
                                        .into(findViewById(R.id.business_logo))

                                    findViewById<TextView>(R.id.business_location).text = locationResponse.location.address.addressLine1 + "\n" + locationResponse.location.address.locality + "\n" + locationResponse.location.address.administrativeDistrictLevel1 + " " + locationResponse.location.address.postalCode;

                                }
                            }

                            override fun onError(e: Throwable?) {
                                runOnUiThread {
                                    Toast.makeText(this@RetrieveMerchantActivity, e?.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onComplete() {
                            }

                        })
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@RetrieveMerchantActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }
}