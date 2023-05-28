package com.squareup.shopx.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.VisibleRegion
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.LoyaltyCardAdapter
import com.squareup.shopx.adapter.OrderListAdapter
import com.squareup.shopx.model.CartUpdateEvent
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse
import com.squareup.shopx.model.GetAllRecordsRequest
import com.squareup.shopx.model.GetOrdersResponse
import com.squareup.shopx.model.LoyaltyCardsLoadingViewEvent
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_profile, null)
        return view

    }




}