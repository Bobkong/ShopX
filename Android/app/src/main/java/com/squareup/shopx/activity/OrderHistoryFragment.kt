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


class OrderHistoryFragment : Fragment() {

    private val TAG = "OrderHistoryFragment"
    private lateinit var loadingView: ConstraintLayout
    private lateinit var orderList: RecyclerView
    private lateinit var noOrderHistory: ConstraintLayout
    private lateinit var goToMap: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_order_history, null)

        loadingView = view.findViewById(R.id.loading_view)
        orderList = view.findViewById(R.id.order_history)
        noOrderHistory = view.findViewById(R.id.no_order_history)
        goToMap = view.findViewById(R.id.go_to_map)
        goToMap.setOnClickListener {
            (requireActivity() as? HomeActivity)?.goToMainMap()
        }
        requestAllOrderHistory()
        return view

    }

    private fun requestAllOrderHistory() {
        ShopXApiService.getInstance().getAllOrders(PreferenceUtils.getUserPhone())
            .subscribe(object: Observer<GetOrdersResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: GetOrdersResponse?) {
                    requireActivity().runOnUiThread {
                        loadingView.visibility = View.GONE
                        if (value == null || value.allOrders.isNullOrEmpty()) {
                            noOrderHistory.visibility = View.VISIBLE
                            return@runOnUiThread
                        }
                        value.let {
                            orderList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                            orderList.adapter = OrderListAdapter(requireActivity(), value.allOrders)
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    requireActivity().runOnUiThread {
                        loadingView.visibility = View.GONE
                        Toast.makeText(requireContext(), e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }





}