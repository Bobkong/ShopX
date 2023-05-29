package com.squareup.shopx.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.CartItemListAdapter
import com.squareup.shopx.consts.Configs.GOOGLE_PAY_MERCHANT_ID
import com.squareup.shopx.model.*
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.CreateOrderRequest.BasePriceMoney
import com.squareup.shopx.netservice.GooglePay.GooglePayChargeClient
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.netservice.SquareAPI.SquareApiService
import com.squareup.shopx.utils.DateUtil
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.Transparent
import com.squareup.shopx.widget.CustomDialog
import com.squareup.shopx.widget.RadiusCardView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import sqip.GooglePay
import java.util.*
import kotlin.collections.ArrayList


class OrderActivity: AppCompatActivity(), CartCallback {
    private val TAG = "OrderActivity"
    private var itemList: RecyclerView? = null
    private lateinit var merchantInfo: ShopXMerchant
    private var loyaltyInfo: GetLoyaltyInfoResponse? = null
    private lateinit var gettingRewardsLayout: RadiusCardView
    private lateinit var subtotalPrice: TextView
    private lateinit var discountValueTv: TextView
    private lateinit var totalPrice: TextView
    private lateinit var placeOrder: TextView
    private lateinit var discountValueCl: ConstraintLayout
    private lateinit var loyaltyCl: ConstraintLayout
    private lateinit var loyaltyValueTv: TextView
    private var orderId: String? = null
    private lateinit var loadingView: ConstraintLayout
    private lateinit var back: ImageView
    private var loyaltyValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        Transparent.transparentNavBar(this)
        Transparent.transparentStatusBar(this, true)

        merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant
        loyaltyInfo = intent.extras?.getSerializable("loyalty") as? GetLoyaltyInfoResponse

        itemList = findViewById(R.id.order_item_list)
        gettingRewardsLayout = findViewById(R.id.redeem_rewards)
        subtotalPrice = findViewById(R.id.subtotal_price)
        discountValueTv = findViewById(R.id.discount_value)
        totalPrice = findViewById(R.id.total_value)
        placeOrder = findViewById(R.id.place_order)
        discountValueCl = findViewById(R.id.discount_cl)
        loadingView = findViewById(R.id.loading_view)
        loyaltyCl = findViewById(R.id.loyalty_cl)
        loyaltyValueTv = findViewById(R.id.loyalty_value)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            finish()
        }

        if (loyaltyInfo != null) {
            gettingRewardsLayout.visibility = View.VISIBLE
        } else {
            gettingRewardsLayout.visibility = View.GONE
        }

        gettingRewardsLayout.setOnClickListener {
            orderId?.let {
                val intent = Intent(this@OrderActivity, RedeemActivity::class.java)
                intent.putExtra("orderId", it)
                intent.putExtra("merchant", merchantInfo)
                intent.putExtra("loyalty", loyaltyInfo)
                startActivity(intent)
            }
        }

        itemList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        itemList?.adapter = CartItemListAdapter(this, merchantInfo, this, CartItemListAdapter.FROM_ORDER)

        updatePrice()

        val lineItems = ArrayList<CreateOrderRequest.LineItem>()
        for (item in AllMerchants.getCartItems(merchantInfo).keys) {
            val basePriceMoney = BasePriceMoney(item.getItemDiscountPrice(merchantInfo).toInt())
            val lineItem = CreateOrderRequest.LineItem(basePriceMoney, AllMerchants.getCountOfAnItem(merchantInfo, item).toString(), item.itemName, item.itemVariationId)
            lineItems.add(lineItem)
        }
        val order = CreateOrderRequest.Order(merchantInfo.locationId, lineItems, CreateOrderRequest.PricingOptions())

        val orderRequest = CreateOrderRequest(UUID.randomUUID().toString(), order)
        SquareApiService.getInstance(merchantInfo.accessToken).createOrder(orderRequest)
            .subscribe(object: Observer<CreateOrderResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: CreateOrderResponse?) {
                    runOnUiThread {
                        value?.let {
                            Log.i(TAG, it.order.id)
                            orderId = it.order.id
                        }
                        loadingView.visibility = View.GONE
                    }

                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
                        loadingView.visibility = View.GONE
                    }
                }

                override fun onComplete() {
                }

            })
        EventBus.getDefault().register(this);

        googlePayChargeClient = GooglePayChargeClient()
        googlePayChargeClient!!.onActivityCreated(this)
        paymentsClient = Wallet.getPaymentsClient(
            this,
            WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()
        )
        placeOrder.setOnClickListener {
            showPayConfirmDialog()
        }

    }

    private fun showPayConfirmDialog() {
        val customDialog = CustomDialog(this, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        rightAction.text = "Pay Order"
        leftActivity.text = "Cancel"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "Please go to the store and pay the order after confirming with the merchant."
        customDialog.show()

        leftActivity.setOnClickListener {
            customDialog.dismiss()
        }

        rightAction.setOnClickListener {
            payOrder()
            customDialog.dismiss()
        }
    }

    private fun payOrder() {
        startGooglePayActivity()
    }

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 1
    private var paymentsClient: PaymentsClient? = null
    private var googlePayChargeClient: GooglePayChargeClient? = null
    private fun startGooglePayActivity() {
        val transactionInfo: TransactionInfo = TransactionInfo.newBuilder()
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setTotalPrice("1.00")
            .setCurrencyCode("USD")
            .build()
        val paymentDataRequest: PaymentDataRequest = GooglePay.createPaymentDataRequest(
            GOOGLE_PAY_MERCHANT_ID,
            transactionInfo
        )
        val googlePayActivityTask: Task<PaymentData> =
            paymentsClient!!.loadPaymentData(paymentDataRequest)
        AutoResolveHelper.resolveTask(
            googlePayActivityTask,
            this,
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            handleGooglePayActivityResult(resultCode, data)
        }
    }

    private fun handleGooglePayActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val paymentData = PaymentData.getFromIntent(data!!)
            if (paymentData != null && paymentData.paymentMethodToken != null) {
                val googlePayToken = paymentData.paymentMethodToken!!.token
                googlePayChargeClient!!.charge(googlePayToken)
            }
        }
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        if (!isChangingConfigurations) {
            googlePayChargeClient!!.cancel()
        }
        googlePayChargeClient!!.onActivityDestroyed()
        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedeemSuccessEvent(event: RedeemSuccessEvent) {
        if (event.orderId == orderId) {
            retrieveOrder()
            loadingView.visibility = View.VISIBLE
        }
    }

    private fun retrieveOrder() {
        if (orderId.isNullOrEmpty()) {
            Toast.makeText(this@OrderActivity, "Order hasn't been created, please try later", Toast.LENGTH_SHORT).show()
            return
        }
        SquareApiService.getInstance(merchantInfo.accessToken).retrieveOrder(orderId)
            .subscribe(object : Observer<CreateOrderResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: CreateOrderResponse?) {
                    runOnUiThread {
                        value?.let {
                            Log.i(TAG, it.order.id)
                            loadingView.visibility = View.GONE
                            loyaltyValue = it.order.discountAmount.amount
                            updatePrice()
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, e?.message, Toast.LENGTH_SHORT).show()
                        loadingView.visibility = View.GONE
                    }
                }

                override fun onComplete() {
                }

            })
    }

    override fun dismissCart() {
        finish()
    }

    override fun updatePrice() {
        subtotalPrice.text = "$ " + String.format(
            "%.2f",
            AllMerchants.getOriginalPrice(merchantInfo) / 100.0
        )

        val discountValue = AllMerchants.getOriginalPrice(merchantInfo) - AllMerchants.getPrice(merchantInfo)
        if (discountValue == 0F) {
            discountValueCl.visibility = View.GONE
        } else {
            discountValueCl.visibility = View.VISIBLE
            discountValueTv.text = "- $ " + String.format(
                "%.2f",
                discountValue / 100.0
            )
        }

        if (loyaltyValue == 0) {
            loyaltyCl.visibility = View.GONE
        } else {
            loyaltyCl.visibility = View.VISIBLE
            loyaltyValueTv.text = "- $ " + String.format(
                "%.2f",
                loyaltyValue / 100.0
            )
        }

        totalPrice.text = "$ " + String.format(
            "%.2f",
            (AllMerchants.getPrice(merchantInfo) - loyaltyValue) / 100.0
        )
    }

    fun payOrder(nonce: String) {
        val arItemList = ARItemList()
        for (item in AllMerchants.getCartItems(merchantInfo).keys) {
            if (!item.arEffectLink.isNullOrEmpty()) {
                arItemList.arItems.add(item)
            }
        }
        val placeOrderRequest = PlaceOrderRequest(PreferenceUtils.getUserPhone(),
            merchantInfo.accessToken,
            orderId,
            System.currentTimeMillis(),
            ((AllMerchants.getPrice(merchantInfo) - loyaltyValue).toInt()),
            AllMerchants.getOriginalPrice(merchantInfo).toInt(),
            (AllMerchants.getOriginalPrice(merchantInfo) - AllMerchants.getPrice(merchantInfo)).toInt(),
            loyaltyValue,
            AllMerchants.getTotalItemCount(merchantInfo),
            DateUtil.getOrderTime())
        sendOrderToShopX(placeOrderRequest)
        runOnUiThread {
            val intent = Intent(this@OrderActivity, PaySuccessActivity::class.java)
            intent.putExtra("merchantInfo", merchantInfo)
            intent.putExtra("loyaltyInfo", loyaltyInfo)
            intent.putExtra("orderId", orderId)
            intent.putExtra("nonce", nonce)
            intent.putExtra("orderInfo", placeOrderRequest)
            intent.putExtra("value", (AllMerchants.getPrice(merchantInfo) - loyaltyValue).toInt())
            intent.putExtra("arItems", arItemList)
            startActivity(intent)
            AllMerchants.clearCart(merchantInfo)
            EventBus.getDefault().post(CartUpdateEvent(merchantInfo))
            finish()
        }
    }

    private fun sendOrderToShopX(placeOrderRequest: PlaceOrderRequest) {

        ShopXApiService.getInstance().placeOrder(placeOrderRequest)
            .subscribe(object : Observer<GeneralResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GeneralResponse?) {
                    // do nothing for now
                }

                override fun onError(e: Throwable?) {
                    // do nothing for now
                }

                override fun onComplete() {
                }

            })
    }
}