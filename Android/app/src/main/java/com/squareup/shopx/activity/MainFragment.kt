package com.squareup.shopx.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.BroadcastReceiverPage
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.widget.BottomMapView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


class MainFragment : Fragment() {

    private val TAG = "MainFragment"



    private var bottomSheet: BottomMapView? = null
    private lateinit var loadingView: ConstraintLayout


    private lateinit var homepageHeader: TextView
    private lateinit var userName: TextView
    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_main, null)

        bottomSheet = view.findViewById(R.id.bottom_sheet)
        bottomSheet?.init(this)

        homepageHeader = view.findViewById(R.id.homepage_header)
        userName = view.findViewById(R.id.user_name)
        loadingView = view.findViewById(R.id.loading_view)

        homepageHeader.text = "Hello " + PreferenceUtils.getUsername()
        var nameString = if (PreferenceUtils.getUsername().length > 1) {
            PreferenceUtils.getUsername().substring(0, 2)
        } else {
            PreferenceUtils.getUsername().substring(0, 1)
        }
        nameString = nameString.toUpperCase()
        userName.text = nameString

        return view
    }

    fun showMap() {
        if (isAdded) {
            bottomSheet?.expandMapView()
        }

    }




    private fun createNotification(merchant: ShopXMerchant) {
        if (isAdded && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("Notify", "ShopX", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "ShopX Discount Notification"
            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            val intent = Intent(requireActivity(), BroadcastReceiverPage::class.java)
            intent.putExtra("merchant", merchant)
            requireActivity().sendBroadcast(intent)
        }
    }

    public fun requestAllMerchants() {
        ShopXApiService.getInstance().allMerchants
            .subscribe(object: Observer<AllMerchantsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AllMerchantsResponse?) {

                    requireActivity().runOnUiThread {
                        requireActivity().runOnUiThread {
                            loadingView.visibility = View.GONE
                        }
                        if (value?.code == 1) {
                            Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        value?.merchants?.let {
                            if (it.size > 0) {
                                AllMerchants.allMerchants = it

                                val closestMerchant = AllMerchants.getClosestMerchant()
                                if (PreferenceUtils.getNotification() && closestMerchant != null && AllMerchants.calculateDistance(closestMerchant.lat, closestMerchant.lng) < 50) {
                                    // if there's a merchant near customer within 50miles, send notification
                                    Handler().postDelayed({
                                        createNotification(closestMerchant)
                                    }, 6 * 1000)
                                }

                            }
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    requireActivity().runOnUiThread {
                        loadingView.visibility = View.GONE
                        Toast.makeText(context, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }


}