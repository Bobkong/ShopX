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

class AllItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_items)

        val merchantToken = intent.extras?.getString("merchantToken")

        SquareApiService.getInstance(merchantToken).retrieveAllItems()
            .subscribe {
                var itemString = ""
                var imageIds = ArrayList<String>()

                for (item in it.objects) {
                    item.itemData.imageIds?.let { imageIdData ->
                        imageIds.add(imageIdData[0])
                    }
                }

                // retrieve image url
                SquareApiService.getInstance(merchantToken).batchRetrieveObjects(imageIds)
                    .subscribe { objectResponse ->

                        for (i in 0 until it.objects.size) {

                            val item = it.objects[i]
                            itemString += "Name: " + item.itemData.name + "\n"
                            itemString += "Description: " + item.itemData.description + "\n"
                            itemString += "Price: " + "$" + item.itemData.variations[0].itemVariationData.priceMoney.amount / 100.00 + "\n"
                            itemString += "Image Url: " + objectResponse.objects[i].imageData.url + "\n\n\n"

                            if (i == it.objects.size - 1) {
                                runOnUiThread {
                                    findViewById<TextView>(R.id.all_items_string).text = itemString
                                }
                            }
                        }
                    }

            }

    }
}