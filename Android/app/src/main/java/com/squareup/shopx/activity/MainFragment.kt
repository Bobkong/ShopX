package com.squareup.shopx.activity

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.RecommendARAdapter
import com.squareup.shopx.adapter.RecommendDiscountAdapter
import com.squareup.shopx.adapter.RecommendLoyaltyAdapter
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.GetLoyaltyInfoResponse
import com.squareup.shopx.model.NotificationEvent
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.widget.BottomMapView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus


class MainFragment : Fragment() {

    private val TAG = "MainFragment"



    private var bottomSheet: BottomMapView? = null
    private lateinit var loadingView: ConstraintLayout
    private lateinit var discountRecommend: TextView
    private lateinit var loyaltyRecommend: TextView
    private lateinit var discountList: RecyclerView
    private lateinit var loyaltyList: RecyclerView
    private lateinit var arList: RecyclerView
    private lateinit var scrollView: NestedScrollView


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

        scrollView = view.findViewById(R.id.scroll_view)
        val layoutParams = scrollView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.height = bottomSheet!!.getDefaultMapTopMargin()
        scrollView.layoutParams = layoutParams

        homepageHeader = view.findViewById(R.id.homepage_header)
        userName = view.findViewById(R.id.user_name)
        loadingView = view.findViewById(R.id.loading_view)

        discountRecommend = view.findViewById(R.id.homepage_discount)
        loyaltyRecommend = view.findViewById(R.id.homepage_loyalty)
        discountList = view.findViewById(R.id.discount_list)
        loyaltyList = view.findViewById(R.id.loyalty_list)
        arList = view.findViewById(R.id.ar_enable_merchants_list)

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



    public fun requestAllMerchants() {
        ShopXApiService.getInstance().allMerchants
            .subscribe(object: Observer<AllMerchantsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AllMerchantsResponse?) {
                    if (!isAdded) {
                        return
                    }
                    requireActivity().runOnUiThread {
                        if (value?.code == 1) {
                            Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        value?.merchants?.let {
                            if (it.size > 0) {
                                AllMerchants.allMerchants = it

                                discountList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                                loyaltyList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                                arList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                                discountList.adapter = RecommendDiscountAdapter(this@MainFragment, AllMerchants.getRecommendMerchants(ShopXMerchant.RECOMMEND_DISCOUNT))
                                arList.adapter = RecommendARAdapter(this@MainFragment, AllMerchants.getAllARMerchants())

                                setRecommendDiscount()

                                discountRecommend.setOnClickListener {
                                    setRecommendDiscount()
                                }

                                loyaltyRecommend.setOnClickListener {
                                    setRecommendLoyalty()
                                }

                                requestAllLoyaltyResponse()

                                val closestMerchant = AllMerchants.getClosestMerchant()
                                if (PreferenceUtils.getNotification() && closestMerchant != null && AllMerchants.calculateDistance(closestMerchant.lat, closestMerchant.lng) < 1) {
                                    // if there's a merchant near customer within 50miles, send notification
                                    Handler().postDelayed({
                                        EventBus.getDefault().post(NotificationEvent(closestMerchant))
                                    }, 90 * 1000)
                                }

                            }
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    if (!isAdded) {
                        return
                    }
                    requireActivity().runOnUiThread {
                        loadingView.visibility = View.GONE
                        Toast.makeText(context, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }


    private fun setRecommendDiscount() {
        discountRecommend.setBackgroundResource(R.drawable.map_filter_selected_background)
        discountRecommend.setTextColor(resources.getColor(R.color.white))
        loyaltyRecommend.setBackgroundResource(R.drawable.map_filter_unselected_background)
        loyaltyRecommend.setTextColor(resources.getColor(R.color.black_0))
        discountList.visibility = View.VISIBLE
        loyaltyList.visibility = View.GONE
    }

    private fun setRecommendLoyalty() {
        loyaltyRecommend.setBackgroundResource(R.drawable.map_filter_selected_background)
        loyaltyRecommend.setTextColor(resources.getColor(R.color.white))
        discountRecommend.setBackgroundResource(R.drawable.map_filter_unselected_background)
        discountRecommend.setTextColor(resources.getColor(R.color.black_0))
        discountList.visibility = View.GONE
        loyaltyList.visibility = View.VISIBLE
    }

    private val loyaltyResponses = HashMap<ShopXMerchant, GetLoyaltyInfoResponse>()
    private fun requestAllLoyaltyResponse() {
        if (AllMerchants.getRecommendMerchants(ShopXMerchant.RECOMMEND_LOYALTY).size == 0) {
            loadingView.visibility = View.GONE
            loyaltyList.visibility = View.GONE
        } else {
            for (merchant in AllMerchants.getRecommendMerchants(ShopXMerchant.RECOMMEND_LOYALTY)) {
                requestLoyaltyInfo(merchant)
            }
        }

    }

    private fun requestLoyaltyInfo(merchant: ShopXMerchant) {
        ShopXApiService.getInstance().getLoyaltyinfo(merchant.accessToken, PreferenceUtils.getUserPhone())
            .subscribe(object : Observer<GetLoyaltyInfoResponse?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(value: GetLoyaltyInfoResponse?) {
                    if (value != null && isAdded) {
                        loyaltyResponses[merchant] = value
                        if (loyaltyResponses.size == AllMerchants.getRecommendMerchants(ShopXMerchant.RECOMMEND_LOYALTY).size) {
                            requireActivity().runOnUiThread {
                                loadingView.visibility = View.GONE
                                loyaltyList.adapter = RecommendLoyaltyAdapter(this@MainFragment, AllMerchants.getRecommendMerchants(ShopXMerchant.RECOMMEND_LOYALTY), loyaltyResponses)
                            }
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    if (isAdded) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                override fun onComplete() {}
            })
    }


}