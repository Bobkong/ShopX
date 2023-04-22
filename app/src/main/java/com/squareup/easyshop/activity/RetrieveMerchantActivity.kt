package com.squareup.easyshop.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.squareup.easyshop.R
import com.squareup.easyshop.consts.Configs
import com.squareup.easyshop.model.MerchantResponse
import com.squareup.easyshop.netservice.SquareAPI.SquareApiService
import io.reactivex.android.schedulers.AndroidSchedulers

class RetrieveMerchantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_merchant)

        val merchantResponse = intent.extras?.getSerializable("merchant") as? MerchantResponse;

        findViewById<TextView>(R.id.business_name).text = merchantResponse?.merchant?.businessName ?: ""

        SquareApiService.getInstance(Configs.token).retrieveLocation(merchantResponse?.merchant?.mainLocationId)
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