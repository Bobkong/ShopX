package com.squareup.easyshop.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.squareup.easyshop.R
import com.squareup.easyshop.netservice.SquareAPI.SquareApiService

class RetrieveMerchantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_merchant)

        val merchantId = intent.extras?.getString("merchantId")
        val merchantToken = intent.extras?.getString("merchantToken")

        SquareApiService.getInstance(merchantToken).retrieveMerchant(merchantId)
            .subscribe {
                runOnUiThread {
                    findViewById<TextView>(R.id.business_name).text = it?.merchant?.businessName ?: ""
                }

                SquareApiService.getInstance(merchantToken).retrieveLocation(it?.merchant?.mainLocationId)
                    .subscribe {

                        runOnUiThread {
                            Glide.with(this)
                                .load(it.location.logoUrl)
                                .into(findViewById(R.id.business_logo))

                            findViewById<TextView>(R.id.business_location).text = it.location.address.addressLine1 + "\n" + it.location.address.locality + "\n" + it.location.address.administrativeDistrictLevel1 + " " + it.location.address.postalCode;

                        }
                    }
            }
    }
}