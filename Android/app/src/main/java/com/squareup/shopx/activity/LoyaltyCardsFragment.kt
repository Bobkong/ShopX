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
import com.squareup.shopx.model.CartUpdateEvent
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse
import com.squareup.shopx.model.LoyaltyCardsLoadingViewEvent
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoyaltyCardsFragment : Fragment() {

    private val TAG = "LoyaltyCardsFragment"

    private lateinit var loyaltyCards: RecyclerView
    private lateinit var lastOne: ImageView
    private lateinit var nextOne: ImageView
    private val snapHelper = PagerSnapHelper()
    private var allLoyaltyRecordsResponse: GetAllLoyaltyRecordsResponse? = null
    private var currentPosition = 0
    private lateinit var noLoyaltyCards: ConstraintLayout
    private lateinit var goToMap: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_loyalty_cards, null)
        loyaltyCards = view.findViewById(R.id.loyalty_cards)
        lastOne = view.findViewById(R.id.last_one)
        nextOne = view.findViewById(R.id.next_one)
        noLoyaltyCards = view.findViewById(R.id.no_loyalty_card)
        goToMap = view.findViewById(R.id.go_to_map)
        goToMap.setOnClickListener {
            (requireActivity() as? HomeActivity)?.goToMainMap()
        }
        requestAllLoyaltyCards()
        return view

    }

    private fun requestAllLoyaltyCards() {
        ShopXApiService.getInstance().getAllLoyaltyRecordsResponse(PreferenceUtils.getUserPhone())
            .subscribe(object: Observer<GetAllLoyaltyRecordsResponse> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: GetAllLoyaltyRecordsResponse?) {
                    requireActivity().runOnUiThread {
                        if (value == null || value.loyaltyMerchants.isNullOrEmpty()) {
                            noLoyaltyCards.visibility = View.VISIBLE
                            return@runOnUiThread
                        }
                        value.let {
                            allLoyaltyRecordsResponse = it
                            val loyaltyCardAdapter = LoyaltyCardAdapter(this@LoyaltyCardsFragment, it.loyaltyMerchants)
                            loyaltyCards.adapter = loyaltyCardAdapter
                            loyaltyCards.layoutManager = LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)
                            snapHelper.attachToRecyclerView(loyaltyCards)
                            setCurrentPosition(0)
                            lastOne.visibility = View.VISIBLE
                            lastOne.setOnClickListener {
                                if (currentPosition != 0) {
                                    loyaltyCards.smoothScrollToPosition(currentPosition - 1)
                                    setCurrentPosition(currentPosition - 1)
                                }
                            }
                            nextOne.visibility = View.VISIBLE
                            nextOne.setOnClickListener {
                                if (currentPosition != value.loyaltyMerchants.size - 1) {
                                    loyaltyCards.smoothScrollToPosition(currentPosition + 1)
                                    setCurrentPosition(currentPosition + 1)
                                }
                            }

                            loyaltyCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                                    super.onScrollStateChanged(recyclerView, newState)
                                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                        val itemView = snapHelper.findSnapView (recyclerView.layoutManager)
                                        var position = -1
                                        if (itemView != null) {
                                            position = recyclerView.getChildAdapterPosition(itemView)
                                        }
                                        setCurrentPosition(position)
                                    }

                                }

                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)
                                }
                            })

                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    requireActivity().runOnUiThread {

                        Toast.makeText(requireContext(), e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    private fun setCurrentPosition(position: Int) {
        currentPosition = position
        if (currentPosition == 0) {
            lastOne.setImageDrawable(resources.getDrawable(R.drawable.last_one_disable))
        } else {
            lastOne.setImageDrawable(resources.getDrawable(R.drawable.last_one_enable))
        }

        if (currentPosition == allLoyaltyRecordsResponse!!.loyaltyMerchants.size - 1) {
            nextOne.setImageDrawable(resources.getDrawable(R.drawable.next_one_disable))
        } else {
            nextOne.setImageDrawable(resources.getDrawable(R.drawable.next_one_enable))
        }
    }


}