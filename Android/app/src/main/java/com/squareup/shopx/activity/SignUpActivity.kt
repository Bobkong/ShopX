package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.shopx.R
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.model.AddCustomerResponse
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.Transparent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.w3c.dom.Text

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpContinue: TextView
    private lateinit var usernameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var createPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var usernameWarning: TextView
    private lateinit var phoneWarning: TextView
    private lateinit var createPasswordWarning: TextView
    private lateinit var confirmPasswordWarning: TextView
    private lateinit var signIn: TextView
    private lateinit var phoneNumberInputLl: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signUpContinue = findViewById(R.id.sign_up_continue)
        usernameEditText = findViewById(R.id.username_input)
        phoneNumberEditText = findViewById(R.id.phone_number_input)
        createPasswordEditText = findViewById(R.id.create_password_input)
        confirmPasswordEditText = findViewById(R.id.confirm_password_input)
        usernameWarning = findViewById(R.id.username_warning)
        phoneWarning = findViewById(R.id.phone_number_warning)
        createPasswordWarning = findViewById(R.id.create_password_warning)
        confirmPasswordWarning = findViewById(R.id.confirm_password_warning)
        signIn = findViewById(R.id.sign_in)
        phoneNumberInputLl = findViewById(R.id.phone_number_input_ll)

        signIn.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpContinue.setOnClickListener {
            tryToSignUp()
        }

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this)
    }

    private fun tryToSignUp() {
        val phone = phoneNumberEditText.text.toString()
        val nickname = usernameEditText.text.toString()
        val password = createPasswordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (nickname.isEmpty()) {
            usernameWarning.visibility = View.VISIBLE
            usernameEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            usernameWarning.text = "Required"
            return
        } else {
            usernameWarning.visibility = View.GONE
            usernameEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10)
        }

        if (phone.isEmpty()) {
            phoneWarning.visibility = View.VISIBLE
            phoneNumberInputLl.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            phoneWarning.text = "Required"
            return
        } else {
            phoneWarning.visibility = View.GONE
            phoneNumberInputLl.background = resources.getDrawable(R.drawable.black_95_long_button_r10)
        }

        if (phone.length != 10) {
            phoneWarning.visibility = View.VISIBLE
            phoneNumberInputLl.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            phoneWarning.text = "Error phone number"
            return
        } else {
            phoneWarning.visibility = View.GONE
            phoneNumberInputLl.background = resources.getDrawable(R.drawable.black_95_long_button_r10)
        }

        if (password.isEmpty()) {
            createPasswordWarning.visibility = View.VISIBLE
            createPasswordEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            createPasswordWarning.text = "Required"
            return
        } else {
            createPasswordWarning.visibility = View.GONE
            createPasswordEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10)
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordWarning.visibility = View.VISIBLE
            confirmPasswordEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            confirmPasswordWarning.text = "Required"
            return
        } else {
            confirmPasswordWarning.visibility = View.GONE
            confirmPasswordEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10)
        }

        if (password != confirmPassword) {
            confirmPasswordWarning.visibility = View.VISIBLE
            confirmPasswordEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10_with_red_border)
            confirmPasswordWarning.text = "Password does not match"
            return
        } else {
            confirmPasswordWarning.visibility = View.GONE
            confirmPasswordEditText.background = resources.getDrawable(R.drawable.black_95_long_button_r10)
        }

        signUp(phone, nickname, password)
    }

    fun signUp(phone: String, nickname: String, password: String) {
        ShopXApiService.getInstance().addCustomer(phone, nickname, password)
            .subscribe(object: Observer<AddCustomerResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AddCustomerResponse?) {
                    runOnUiThread {
                        if (value?.code == 1) {
                            Toast.makeText(this@SignUpActivity, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        Log.i("SignUpActivity", value?.msg ?: "")
                        PreferenceUtils.setUserPhone(phone)
                        val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }

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