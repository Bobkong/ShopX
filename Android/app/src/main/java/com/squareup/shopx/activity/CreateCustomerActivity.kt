package com.squareup.shopx.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.squareup.shopx.R
import com.squareup.shopx.model.Customer
import com.squareup.shopx.model.CustomerResponse
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.UUID

class CreateCustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_customer)

        val merchantToken = intent.extras?.getString("merchantToken")

        val nickNameEditText = findViewById<EditText>(R.id.nickname_input)
        val phoneNumberEditText = findViewById<EditText>(R.id.phone_number_input)

        findViewById<TextView>(R.id.create_customer_button).setOnClickListener {
            if (nickNameEditText.text.toString().isNotEmpty() && phoneNumberEditText.text.toString().isNotEmpty()) {
                val customer = Customer(nickNameEditText.text.toString(), phoneNumberEditText.text.toString(), null, UUID.randomUUID().toString())
                SquareApiService.getInstance(merchantToken).createCustomer(customer)
                    .subscribe(object : Observer<CustomerResponse> {
                        override fun onNext(customerResponse: CustomerResponse?) {

                        }

                        override fun onError(e: Throwable?) {
                            runOnUiThread {
                                Toast.makeText(this@CreateCustomerActivity, e?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onSubscribe(d: Disposable?) {
                        }

                        override fun onComplete() {
                        }

                    })
            }
        }

    }
}

