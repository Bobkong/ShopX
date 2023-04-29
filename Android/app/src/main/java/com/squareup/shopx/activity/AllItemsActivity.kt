package com.squareup.shopx.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.squareup.shopx.R
import com.squareup.shopx.model.ObjectsResponse
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class AllItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_items)

        val merchantToken = intent.extras?.getString("merchantToken")

        SquareApiService.getInstance(merchantToken).retrieveAllItems()
            .subscribe(object : Observer<ObjectsResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(response: ObjectsResponse) {
                    var itemString = ""
                    var imageIds = ArrayList<String>()

                    for (item in response.objects) {
                        item.itemData.imageIds?.let { imageIdData ->
                            imageIds.add(imageIdData[0])
                        }
                    }

                    // retrieve image url
                    SquareApiService.getInstance(merchantToken).batchRetrieveObjects(imageIds)
                        .subscribe(object: Observer<ObjectsResponse> {
                            override fun onSubscribe(d: Disposable?) {
                            }

                            override fun onNext(imageResponse: ObjectsResponse) {
                                for (i in 0 until response.objects.size) {

                                    val item = response.objects[i]
                                    itemString += "Name: " + item.itemData.name + "\n"
                                    itemString += "Description: " + item.itemData.description + "\n"
                                    itemString += "Price: " + "$" + item.itemData.variations[0].itemVariationData.priceMoney.amount / 100.00 + "\n"
                                    itemString += "Image Url: " + imageResponse.objects[i].imageData.url + "\n\n\n"

                                    if (i == response.objects.size - 1) {
                                        runOnUiThread {
                                            findViewById<TextView>(R.id.all_items_string).text = itemString
                                        }
                                    }
                                }
                            }

                            override fun onError(e: Throwable?) {
                                runOnUiThread {
                                    Toast.makeText(this@AllItemsActivity, e?.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onComplete() {
                            }

                        })
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@AllItemsActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })

    }
}