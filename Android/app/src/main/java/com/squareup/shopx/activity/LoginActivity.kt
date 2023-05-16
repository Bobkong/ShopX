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
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.widget.CustomDialog
import com.squareup.shopx.widget.VerifyCodeView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneNumberInputLl: EditText
    private lateinit var signinContinue: TextView
    private lateinit var signUp: TextView
    private lateinit var phoneWarning: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberInputLl = findViewById(R.id.phone_number_input)
        signinContinue = findViewById(R.id.sign_in_continue)
        signUp = findViewById(R.id.sign_up)
        phoneWarning = findViewById(R.id.phone_number_warning)

        signinContinue.setOnClickListener {

            tryToSignIn()

        }

    }

    fun checkCustomer(phone: String) {
        ShopXApiService.getInstance().checkCustomer("+1$phone")
            .subscribe(object: Observer<GeneralResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GeneralResponse?) {
                    runOnUiThread {
                        if (value?.code == 0) {
                            showDialog()
                            return@runOnUiThread
                        }

                        getVerificationCode(phone)

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

    fun showDialog() {
        val customDialog = CustomDialog(this, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        leftActivity.visibility = View.GONE
        rightAction.text = "OK"
        dialogTitle.text = "Sign In Failed"
        dialogDesc.text = "The phone number doesn't exist. Please sign up!"
        customDialog.show()

        rightAction.setOnClickListener {
            customDialog.dismiss()
        }
    }

    private fun tryToSignIn() {
        val phone = phoneNumberInputLl.text.toString()
        if (phone.isEmpty()) {
            phoneWarning.visibility = View.VISIBLE
            phoneNumberInputLl.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            phoneWarning.text = "Required"
            return
        }

        if (phone.length != 10) {
            phoneWarning.visibility = View.VISIBLE
            phoneNumberInputLl.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            phoneWarning.text = "Error phone number"
            return
        }

        checkCustomer(phone)
    }

    private fun getVerificationCode(phoneNumber: String) {
        val intent = Intent(this@LoginActivity, VerificationCodeActivity::class.java)
        intent.putExtra("phone", "+1$phoneNumber")
        startActivity(intent)
    }

}