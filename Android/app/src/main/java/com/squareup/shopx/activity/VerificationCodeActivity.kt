package com.squareup.shopx.activity

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.shopx.R
import com.squareup.shopx.model.AddCustomerResponse
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.VerifyCodeView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class VerificationCodeActivity : AppCompatActivity() {
    private lateinit var verifyCodeView: VerifyCodeView
    private var code: String? = null
    private lateinit var codeWarning: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_code)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)

        verifyCodeView = findViewById(R.id.verify_code_view)
        codeWarning = findViewById(R.id.code_warning)
        verifyCodeView.setInputCompleteListener(object: VerifyCodeView.InputCompleteListener {
            override fun inputComplete() {
                if (code.isNullOrEmpty() || verifyCodeView.editContent != code) {
                    codeWarning.visibility = View.VISIBLE
                    codeWarning.text = "Incorrect code"
                } else {

                    if (intent.getStringExtra("nickname").isNullOrEmpty()) {
                        login()
                    } else {
                        saveCustomer()
                    }

                }
            }

            override fun invalidContent() {
                // do noting
            }

        })

        ShopXApiService.getInstance().verifyPhone(intent.getStringExtra("phone"))
            .subscribe(object: Observer<GeneralResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: GeneralResponse?) {
                    runOnUiThread {
                        code = value?.msg
                        Toast.makeText(this@VerificationCodeActivity, "Verification code sent!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@VerificationCodeActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun login() {
        ShopXApiService.getInstance().login(intent.getStringExtra("phone"))
            .subscribe(object : Observer<GeneralResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GeneralResponse?) {
                    if (value?.code == 0) {
                        PreferenceUtils.setUserPhone(intent.getStringExtra("phone")!!)
                        PreferenceUtils.setUsername(value.msg)
                        val intent = Intent(this@VerificationCodeActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@VerificationCodeActivity, value?.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@VerificationCodeActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun saveCustomer() {
        ShopXApiService.getInstance().addCustomer(intent.getStringExtra("phone")!!, intent.getStringExtra("nickname")!!, intent.getStringExtra("password")!!)
            .subscribe(object : Observer<AddCustomerResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: AddCustomerResponse?) {
                    if (value?.code == 0) {
                        PreferenceUtils.setUserPhone(intent.getStringExtra("phone")!!)
                        PreferenceUtils.setUsername(intent.getStringExtra("nickname")!!)
                        val intent = Intent(this@VerificationCodeActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@VerificationCodeActivity, value?.msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@VerificationCodeActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })

    }

}