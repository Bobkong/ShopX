package com.squareup.easyshop.activity

import android.app.Activity
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.squareup.easyshop.R
import com.squareup.easyshop.consts.Configs
import com.squareup.easyshop.netservice.SquareAPI.SquareApiService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)

//        findViewById<TextView>(R.id.verify_phone_button).setOnClickListener {
//            // Start listening for SMS User Consent broadcasts from senderPhoneNumber
//            // The Task<Void> will be successful if SmsRetriever was able to start
//            // SMS User Consent, and will error if there was an error starting.
//            val task = SmsRetriever.getClient(this@LoginActivity).startSmsUserConsent(findViewById<EditText>(R.id.phone_number_input).text.toString())
//        }

        SmsRetriever.getClient(this).startSmsUserConsent(null)

    }

    private val REQUEST_CODE = 1
    private val SMS_CONSENT_REQUEST = 2  // Set to an unused request code
    private val smsVerificationReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action
                && intent.extras != null
            ) {
                val status = intent.extras?.get(SmsRetriever.EXTRA_STATUS) as Status
                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent =
                            intent.extras?.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            consentIntent?.let { startActivityForResult(it, REQUEST_CODE) }
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.d("Login Activity", "timeout")
                    }
                }
            }
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            REQUEST_CODE ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE) ?: return
                    val oneTimeCode = parseOneTimeCode(message)

                    findViewById<EditText>(R.id.verify_phone_button).setText(oneTimeCode)
                }
        }
    }

    private fun parseOneTimeCode(message: String): String {
        return message.split("\n")[0].split(":")[1]
    }
}