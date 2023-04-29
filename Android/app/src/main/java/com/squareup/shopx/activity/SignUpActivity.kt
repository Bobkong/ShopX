package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.shopx.R
import com.squareup.shopx.Util.PreferenceUtils
import com.squareup.shopx.consts.Configs
import com.squareup.shopx.model.AddCustomerResponse
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        findViewById<TextView>(R.id.sign_in).setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.get_verification_code).setOnClickListener {
            val phone = findViewById<EditText>(R.id.phone_number_input).text.toString()
            val nickname = findViewById<EditText>(R.id.nickname_input).text.toString()
            val password = findViewById<EditText>(R.id.password_input).text.toString()

            if (phone.isEmpty() || nickname.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ShopXApiService.getInstance().verifyPhone("+1$phone")
                .subscribe(object: Observer<GeneralResponse> {
                    override fun onSubscribe(d: Disposable?) {

                    }

                    override fun onNext(value: GeneralResponse?) {
                        runOnUiThread {
                            if (value?.code == 1) {
                                Toast.makeText(this@SignUpActivity, value?.msg, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@SignUpActivity, "The verification code has been sent. Please view you SMS inbox.", Toast.LENGTH_SHORT).show()
                                findViewById<TextView>(R.id.verify).setOnClickListener {
                                    val code = findViewById<EditText>(R.id.verify_code_input).text.toString()
                                    if (code.isEmpty() || code != value?.msg) {
                                        Toast.makeText(this@SignUpActivity, "The verification code is wrong", Toast.LENGTH_SHORT).show()
                                    } else {
                                        signUp("+1$phone", nickname, password)
                                    }
                                }
                            }

                        }

                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onComplete() {
                    }

                })



        }

    }

    fun signUp(phone: String, nickname: String, password: String) {
        ShopXApiService.getInstance().addCustomer(phone, nickname, password)
            .subscribe(object: Observer<AddCustomerResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AddCustomerResponse?) {
                    Log.i("SignUpActivity", value?.msg ?: "")
                    PreferenceUtils.setUserPhone(phone)
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

}