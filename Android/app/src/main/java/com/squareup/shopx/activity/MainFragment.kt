package com.squareup.shopx.activity

import android.Manifest
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.exlyo.gmfmt.FloatingMarkerTitlesOverlay
import com.exlyo.gmfmt.MarkerInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.MerchantListAdapter
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.BroadcastReceiverPage
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.utils.UIUtils
import com.squareup.shopx.widget.BottomMapView
import com.squareup.shopx.widget.MapMaskCL
import com.squareup.shopx.widget.customizedseekbar.IndicatorSeekBar
import com.squareup.shopx.widget.customizedseekbar.OnSeekChangeListener
import com.squareup.shopx.widget.customizedseekbar.SeekParams
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


class MainFragment : Fragment() {

    private val TAG = "MainFragment"



    private var bottomSheet: BottomMapView? = null


    private lateinit var homepageHeader: TextView
    private lateinit var userName: TextView

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




    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("Notify", "ShopX", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "ShopX Discount Notification"
            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            val intent = Intent(requireActivity(), BroadcastReceiverPage::class.java)
            requireActivity().sendBroadcast(intent)
        }
    }


}