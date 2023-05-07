package com.squareup.shopx.activity

import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.shopx.R
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<TextView>(R.id.login_button).setOnClickListener {
            val phone = findViewById<EditText>(R.id.phone_number_input).text.toString()
            val password = findViewById<EditText>(R.id.password_input).text.toString()

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ShopXApiService.getInstance().login("+1$phone", password)
                .subscribe(object: Observer<GeneralResponse> {
                    override fun onSubscribe(d: Disposable?) {

                    }

                    override fun onNext(value: GeneralResponse?) {

                        runOnUiThread {
                            if (value?.code == 1) {
                                Toast.makeText(this@LoginActivity, value.msg, Toast.LENGTH_SHORT).show()
                            } else {
                                Log.i("SignUpActivity", value?.msg ?: "")
                                PreferenceUtils.setUserPhone("+1$phone")
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent)
                            }

                        }

                    }

                    override fun onError(e: Throwable?) {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, e?.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onComplete() {
                    }

                })
        }

    }

}