package com.squareup.shopx.widget

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.activity.HomeActivity
import com.squareup.shopx.activity.MainFragment
import com.squareup.shopx.adapter.MerchantListAdapter
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.UIUtils
import com.squareup.shopx.widget.customizedseekbar.IndicatorSeekBar
import com.squareup.shopx.widget.customizedseekbar.OnSeekChangeListener
import com.squareup.shopx.widget.customizedseekbar.SeekParams
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


class BottomMapView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    private val TAG = "BottomMapView"
    var isExpanded = false
    var DownY = 0f
    var moveY = 0f
    var currentMS: Long = 0
    var mainFragment: MainFragment? = null

    private lateinit var mMap: GoogleMap
    private var mapMarkers = HashMap<Marker, String>()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var floatingMarkersOverlay: FloatingMarkerTitlesOverlay? = null
    private val snapHelper = PagerSnapHelper()
    private var mapMaskCL: MapMaskCL? = null
    private var merchantListView: RecyclerView? = null
    private var openNowFilter: TextView? = null
    private var ARFilter: TextView? = null
    private var offerTypeFilter: LinearLayout? = null
    private var distanceFilter: LinearLayout? = null
    private var offerTypeText: TextView? = null
    private var offerTypeTriangle: ImageView? = null
    private var distanceText: TextView? = null
    private var distanceTriangle: ImageView? = null
    private var myAnchor: Marker? = null

    fun init(fragment: MainFragment) {
        mainFragment = fragment
        mapMaskCL = findViewById(R.id.pull_up_cl)

        // filters
        openNowFilter = findViewById(R.id.open_now_filter)
        ARFilter = findViewById(R.id.ar_filter)
        offerTypeFilter = findViewById(R.id.offer_type_filter)
        offerTypeText = findViewById(R.id.offer_type_filter_text)
        distanceFilter = findViewById(R.id.distance_filter)
        distanceText = findViewById(R.id.distance_filter_text)
        offerTypeTriangle = findViewById(R.id.offer_type_triangle)
        distanceTriangle = findViewById(R.id.distance_triangle)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext())
        merchantListView = findViewById(R.id.merchant_list)
        merchantListView?.layoutManager = LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)
        snapHelper.attachToRecyclerView(merchantListView)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = SupportMapFragment()
        val transaction: FragmentTransaction = mainFragment!!.childFragmentManager.beginTransaction()
        transaction.add(R.id.map_container, mapFragment).commit()
        mapFragment.getMapAsync(this)

        val layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = getDefaultMapTopMargin()
        this.layoutParams = layoutParams

        findViewById<ImageView>(R.id.back).setOnClickListener {
            collapseMapView()
        }

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                DownY = event.y //float DownY
                moveY = 0f
                currentMS = System.currentTimeMillis() //long currentMS     获取系统时间
            }
            MotionEvent.ACTION_MOVE -> {
                if (mainFragment == null)
                    return false;
                moveY += event.y - DownY //y轴距离
                if (!isExpanded && moveY < 0 && moveY > -30) {
                    moveMapView(moveY)
                } else if (!isExpanded && moveY < -30) {
                    expandMapView()
                    isExpanded = true
                }
                DownY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (mainFragment == null)
                    return false
                val moveTime = System.currentTimeMillis() - currentMS //移动时间
                //判断是否继续传递信号
                if (moveTime > 100 && moveY < -30 && !isExpanded) {
                    expandMapView()
                    isExpanded = true
                    return true //不再执行后面的事件，在这句前可写要执行的触摸相关代码。点击事件是发生在触摸弹起后
                }
            }
        }
        return false //继续执行后面的代码
    }


    fun getDefaultMapTopMargin(): Int {
        // have bottom bar icons
        if (UIUtils.getNavigationBarHeight(mainFragment!!.requireActivity()) > 60) {
            return UIUtils.getHeight(context) - UIUtils.dp2px(context, 124f)
        } else {
            return UIUtils.getHeight(context) - UIUtils.dp2px(context, 148f)
        }
    }

    fun expandMapView() {
        (mainFragment!!.requireActivity() as? HomeActivity)?.hideNavigationBar()
        mapMaskCL?.visibility = View.GONE
        val currentParams = layoutParams as ConstraintLayout.LayoutParams
        val animator: ValueAnimator = ValueAnimator.ofInt(currentParams.topMargin, 0)
        animator.duration = 180
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            val layoutParams = layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = it.animatedValue as Int
            this.layoutParams = layoutParams

            if ((it.animatedValue as Int) == 0) {
                requestAllMerchants()
            }
        }
        animator.start()
    }

    private fun collapseMapView() {
        val animator: ValueAnimator = ValueAnimator.ofInt(0, getDefaultMapTopMargin())
        animator.duration = 180
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            val layoutParams = layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = it.animatedValue as Int
            this.layoutParams = layoutParams

            if ((it.animatedValue as Int) == getDefaultMapTopMargin()) {
                isExpanded = false
                mapMaskCL?.visibility = View.VISIBLE
                (mainFragment!!.requireActivity() as? HomeActivity)?.showNavigationBar()
            }
        }
        resetBottomView()
        animator.start()
    }

    private fun resetBottomView() {
        AllMerchants.offerType = AllMerchants.OFFER_SEE_ALL
        AllMerchants.distanceLimit = 15F
        updateFilter()
        hideMerchantList()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(
            MapStyleOptions(
                resources
                    .getString(R.string.style_json)
            )
        )

        mMap.setOnMarkerClickListener(this)

        floatingMarkersOverlay = findViewById<FloatingMarkerTitlesOverlay>(R.id.map_floating_markers_overlay)
        floatingMarkersOverlay?.setSource(mMap)

        if (context == null) {
            return
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient!!.lastLocation
            .addOnSuccessListener(mainFragment!!.requireActivity()) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {

                    //createMyMarker(LatLng(location.latitude, location.longitude))
                    AllMerchants.myLat = location.latitude.toFloat()
                    AllMerchants.myLng = location.longitude.toFloat()
                    moveCamera(LatLng(location.latitude, location.longitude))
                } else {
                    Toast.makeText(context, "Loading location failed. Please try later", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun animateCamera(latLng: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15.0f
            )
        )
        floatingMarkersOverlay?.invalidate()
    }

    fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15.0f
            )
        )
        floatingMarkersOverlay?.invalidate()
    }

    fun addMarkersToMap(merchants: List<AllMerchantsResponse.ShopXMerchant>, focusMerchantId: String) {
        mMap.clear()
        mapMarkers.clear()
        //createMyMarker(LatLng(AllMerchants.myLat.toDouble(), AllMerchants.myLng.toDouble()))
        floatingMarkersOverlay?.clearMarkers()
        for (i in merchants.indices) {
            val id: Long = i.toLong() + 1
            val latLng = LatLng(merchants[i].lat.toDouble(), merchants[i].lng.toDouble())
            val title = merchants[i].businessName
            val mi = MarkerInfo(latLng, title, resources.getColor(R.color.black_20))
            val bmp = createColoredMarkerBitmap(
                merchants[i].merchantId == focusMerchantId, merchants[i]
            )
            val marker = mMap.addMarker(
                MarkerOptions().position(mi.coordinates).icon(
                    BitmapDescriptorFactory.fromBitmap(bmp)))
            marker?.let {
                mapMarkers[it] = merchants[i].merchantId
            }
            floatingMarkersOverlay?.addMarker(id, mi)
        }
    }

    private fun createColoredMarkerBitmap(isSelected: Boolean, merchant: AllMerchantsResponse.ShopXMerchant): Bitmap {
        var height = 0
        var width = 0
        var resourceId: Int? = null

        if (isSelected) {
            if (merchant.ifLoyalty == 1 && !merchant.discountType.isNullOrEmpty()) {
                resourceId = R.drawable.selected_loyalty_discount
                height = UIUtils.dp2px(context, 48f)
                width = UIUtils.dp2px(context, 54f)
            } else if (merchant.ifLoyalty == 1) {
                resourceId = R.drawable.selected_loyalty
                height = UIUtils.dp2px(context, 48f)
                width = UIUtils.dp2px(context, 30f)
            } else if (!merchant.discountType.isNullOrEmpty()) {
                resourceId = R.drawable.selected_discount
                height = UIUtils.dp2px(context, 48f)
                width = UIUtils.dp2px(context, 30f)
            } else {
                resourceId = R.drawable.selected_normal
                height = UIUtils.dp2px(context, 48f)
                width = UIUtils.dp2px(context, 30f)
            }
        }

        if (!isSelected) {
            if (merchant.ifLoyalty == 1 && !merchant.discountType.isNullOrEmpty()) {
                resourceId = R.drawable.unselected_loyalty_discount
                height = UIUtils.dp2px(context, 42f)
                width = UIUtils.dp2px(context, 48f)
            } else if (merchant.ifLoyalty == 1) {
                resourceId = R.drawable.unselected_loyalty
                height = UIUtils.dp2px(context, 42f)
                width = UIUtils.dp2px(context, 28f)
            } else if (!merchant.discountType.isNullOrEmpty()) {
                resourceId = R.drawable.unselected_discount
                height = UIUtils.dp2px(context, 42f)
                width = UIUtils.dp2px(context, 28f)
            } else {
                resourceId = R.drawable.unselected_normal
                height = UIUtils.dp2px(context, 42f)
                width = UIUtils.dp2px(context, 28f)
            }
        }


        val bitmapdraw: BitmapDrawable =
            resources.getDrawable(resourceId!!) as BitmapDrawable
        val b: Bitmap = bitmapdraw.getBitmap()
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    private fun myAnchorBitmap(): Bitmap {

        val bitmapdraw: BitmapDrawable =
            resources.getDrawable(R.drawable.my_anchor) as BitmapDrawable
        val b: Bitmap = bitmapdraw.getBitmap()
        return Bitmap.createScaledBitmap(b, UIUtils.dp2px(context, 88f), UIUtils.dp2px(context, 88f), false)
    }

    /** Creates and adds a 2D anchor marker on the 2D map view.  */
    private fun createMyMarker(
        latLng: LatLng
    ) {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .draggable(false)
            .flat(true)
            .visible(true)
            .icon(BitmapDescriptorFactory.fromBitmap(myAnchorBitmap()))
        myAnchor = mMap.addMarker(markerOptions)

        mMap.setOnCameraMoveListener {
        }

    }


    private fun requestAllMerchants() {
        ShopXApiService.getInstance().getAllMerchants()
            .subscribe(object: Observer<AllMerchantsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AllMerchantsResponse?) {

                    mainFragment!!.requireActivity().runOnUiThread {
                        if (value?.code == 1) {
                            Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        value?.merchants?.let {
                            if (it.size > 0) {
                                AllMerchants.allMerchants = it
                                generateMerchantMarkers()
                                // move to the first merchant
                                if (AllMerchants.getDisplayMerchants().size > 0) {
                                    moveCamera(LatLng(
                                        AllMerchants.getDisplayMerchants()[0].lat.toDouble(),
                                        AllMerchants.getDisplayMerchants()[0].lng.toDouble()
                                    ))
                                }

                                setFilterListeners()
                            }
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    mainFragment!!.requireActivity().runOnUiThread {
                        Toast.makeText(context, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    var currentFocusedMarker = 0;
    fun generateMerchantListView(merchants: List<AllMerchantsResponse.ShopXMerchant>) {

        merchantListView?.visibility = View.VISIBLE
        val merchantAdapter = MerchantListAdapter(merchants, mainFragment!!.requireActivity())
        merchantListView?.adapter = merchantAdapter

        merchantListView?.clearOnScrollListeners()
        merchantListView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val itemView = snapHelper.findSnapView (recyclerView.layoutManager)
                    var position = -1
                    if (itemView != null) {
                        position = recyclerView.getChildAdapterPosition(itemView)
                    }
                    if (position != currentFocusedMarker && position < AllMerchants.getDisplayMerchants().size) {
                        Log.i(TAG, position.toString())
                        currentFocusedMarker = position
                        animateCamera(
                            LatLng(
                                merchants[currentFocusedMarker].lat.toDouble(),
                                merchants[currentFocusedMarker].lng.toDouble()
                            )
                        )
                        generateMerchantMarkers(merchants[currentFocusedMarker].merchantId)
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

    }

    var onlySeeOpenNow = false
    fun setFilterListeners() {

        openNowFilter?.setOnClickListener {
            onlySeeOpenNow = !onlySeeOpenNow
            setOpenNow(onlySeeOpenNow)
            hideMerchantList()
        }

        offerTypeFilter?.setOnClickListener {
            showOfferFilter()
            hideMerchantList()
        }

        distanceFilter?.setOnClickListener {
            showDistanceFilter()
            hideMerchantList()
        }

        ARFilter?.setOnClickListener {
            setAREnable()
            hideMerchantList()
        }
    }

    fun setAREnable() {
        AllMerchants.onlySeeAREnable = !AllMerchants.onlySeeAREnable
        if (AllMerchants.onlySeeAREnable) {
            ARFilter?.setBackgroundResource(R.drawable.map_filter_selected_background)
            ARFilter?.setTextColor(resources.getColor(R.color.white))
        } else {
            ARFilter?.setBackgroundResource(R.drawable.map_filter_unselected_background)
            ARFilter?.setTextColor(resources.getColor(R.color.black_0))
        }
        generateMerchantMarkers()
    }

    fun setOpenNow(openNow: Boolean) {
        if (openNow) {
            openNowFilter?.setBackgroundResource(R.drawable.map_filter_selected_background)
            openNowFilter?.setTextColor(resources.getColor(R.color.white))
        } else {
            openNowFilter?.setBackgroundResource(R.drawable.map_filter_unselected_background)
            openNowFilter?.setTextColor(resources.getColor(R.color.black_0))
        }
    }

    private fun showOfferFilter() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogStyle)
        val dialogView = mainFragment!!.layoutInflater.inflate(R.layout.offer_type_filter_bottom_sheet, null, false)
        bottomSheetDialog.setContentView(dialogView)

        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView!!.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val viewResult = dialogView.findViewById<TextView>(R.id.view_distance_result)
        val resetResult = dialogView.findViewById<TextView>(R.id.reset_distance)

        val seeLoyalty = dialogView.findViewById<TextView>(R.id.loyalty_type)
        val seeDiscount = dialogView.findViewById<TextView>(R.id.discount_type)
        val seeDiscountLoyalty = dialogView.findViewById<TextView>(R.id.discount_loyalty_type)

        when (AllMerchants.offerType) {
            AllMerchants.OFFER_SEE_LOYALTY -> {
                setFilterStyle(seeLoyalty, true)
            }
            AllMerchants.OFFER_SEE_DISCOUNT -> {
                setFilterStyle(seeDiscount, true)
            }
            AllMerchants.OFFER_SEE_DISCOUNT_LOYALTY -> {
                setFilterStyle(seeDiscountLoyalty, true)
            }
        }

        var offerType = AllMerchants.offerType

        seeLoyalty.setOnClickListener {
            offerType = AllMerchants.OFFER_SEE_LOYALTY
            setFilterStyle(seeLoyalty, true)
            setFilterStyle(seeDiscount, false)
            setFilterStyle(seeDiscountLoyalty, false)
        }

        seeDiscount.setOnClickListener {
            offerType = AllMerchants.OFFER_SEE_DISCOUNT
            setFilterStyle(seeLoyalty, false)
            setFilterStyle(seeDiscount, true)
            setFilterStyle(seeDiscountLoyalty, false)
        }

        seeDiscountLoyalty.setOnClickListener {
            offerType = AllMerchants.OFFER_SEE_DISCOUNT_LOYALTY
            setFilterStyle(seeLoyalty, false)
            setFilterStyle(seeDiscount, false)
            setFilterStyle(seeDiscountLoyalty, true)
        }

        viewResult.setOnClickListener {
            AllMerchants.offerType = offerType
            generateMerchantMarkers()
            bottomSheetDialog.dismiss()
            updateFilter()
        }

        resetResult.setOnClickListener {
            AllMerchants.offerType = AllMerchants.OFFER_SEE_ALL
            generateMerchantMarkers()
            bottomSheetDialog.dismiss()
            updateFilter()
        }

        bottomSheetDialog.show()
    }

    private fun updateFilter() {

        // offer type
        if (AllMerchants.offerType == AllMerchants.OFFER_SEE_ALL) {
            offerTypeFilter?.setBackgroundResource(R.drawable.map_filter_unselected_background)
            offerTypeText?.setTextColor(resources.getColor(R.color.black_0))
            offerTypeTriangle?.setImageResource(R.drawable.filter_triangle)
            offerTypeText?.text = "Offer Type"
        } else {
            offerTypeFilter?.setBackgroundResource(R.drawable.map_filter_selected_background)
            offerTypeText?.setTextColor(resources.getColor(R.color.white))
            offerTypeTriangle?.setImageResource(R.drawable.filter_triangle_selected)
            offerTypeText?.text = when (AllMerchants.offerType) {
                AllMerchants.OFFER_SEE_DISCOUNT -> {
                    "Discount"
                }
                AllMerchants.OFFER_SEE_LOYALTY -> {
                    "Loyalty"
                }
                else -> {
                    "Discount & Loyalty"
                }
            }
        }

        // distance
        if (AllMerchants.distanceLimit == Float.MAX_VALUE) {
            distanceFilter?.setBackgroundResource(R.drawable.map_filter_unselected_background)
            distanceText?.setTextColor(resources.getColor(R.color.black_0))
            distanceTriangle?.setImageResource(R.drawable.filter_triangle)
            distanceText?.text = "Distance"
        } else {
            distanceFilter?.setBackgroundResource(R.drawable.map_filter_selected_background)
            distanceText?.setTextColor(resources.getColor(R.color.white))
            distanceTriangle?.setImageResource(R.drawable.filter_triangle_selected)
            distanceText?.text = "0~" + AllMerchants.distanceLimit.toInt() + " miles"
        }
    }

    private fun setFilterStyle(textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.map_filter_selected_background)
            textView.setTextColor(resources.getColor(R.color.white))
        } else {
            textView.setBackgroundResource(R.drawable.map_filter_unselected_background)
            textView.setTextColor(resources.getColor(R.color.black_0))
        }
    }

    private fun showDistanceFilter() {
        var distance = AllMerchants.distanceLimit
        if (distance == Float.MAX_VALUE) {
            distance = 0F
        }
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogStyle)
        val dialogView = mainFragment!!.layoutInflater.inflate(R.layout.distance_filter_bottom_sheet, null, false)
        bottomSheetDialog.setContentView(dialogView)

        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView!!.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val seekBar = dialogView.findViewById<IndicatorSeekBar>(R.id.distance_filter)
        seekBar.setProgress(distance)
        setDistanceFilterTextColor(distance, dialogView)

        seekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                setDistanceFilterTextColor(seekBar!!.progress.toFloat(), dialogView)
                distance = calculateProgress(seekBar.progress.toFloat())
            }

        }

        val viewResult = dialogView.findViewById<TextView>(R.id.view_distance_result)
        val resetResult = dialogView.findViewById<TextView>(R.id.reset_distance)

        viewResult.setOnClickListener {
            AllMerchants.distanceLimit = distance
            generateMerchantMarkers()
            bottomSheetDialog.dismiss()
            updateFilter()
        }

        resetResult.setOnClickListener {
            distance = Float.MAX_VALUE
            AllMerchants.distanceLimit = distance
            generateMerchantMarkers()
            bottomSheetDialog.dismiss()
            updateFilter()
        }

        bottomSheetDialog.show()
    }

    private fun calculateProgress(progress: Float): Float {
        if (progress <= 7.5) {
            return Float.MAX_VALUE
        }
        if (progress <= 22.5) {
            return 15f
        }
        if (progress <= 37.5) {
            return 30f
        }
        if (progress <= 52.5) {
            return 45f
        } else {
            return 60f
        }
    }

    fun setDistanceFilterTextColor(progress: Float, dialogView: View) {
        val thirtyMi = dialogView.findViewById<TextView>(R.id.distance_filter_30)
        val fortyFiveMi = dialogView.findViewById<TextView>(R.id.distance_filter_45)
        val sixtyMi = dialogView.findViewById<TextView>(R.id.distance_filter_60)

        if (progress < 23) {
            thirtyMi.setTextColor(resources.getColor(R.color.black_80))
            fortyFiveMi.setTextColor(resources.getColor(R.color.black_80))
            sixtyMi.setTextColor(resources.getColor(R.color.black_80))
        } else if (progress < 38) {
            thirtyMi.setTextColor(resources.getColor(R.color.black_0))
            fortyFiveMi.setTextColor(resources.getColor(R.color.black_80))
            sixtyMi.setTextColor(resources.getColor(R.color.black_80))
        } else if (progress < 53) {
            thirtyMi.setTextColor(resources.getColor(R.color.black_0))
            fortyFiveMi.setTextColor(resources.getColor(R.color.black_0))
            sixtyMi.setTextColor(resources.getColor(R.color.black_80))
        } else if (progress >= 53) {
            thirtyMi.setTextColor(resources.getColor(R.color.black_0))
            fortyFiveMi.setTextColor(resources.getColor(R.color.black_0))
            sixtyMi.setTextColor(resources.getColor(R.color.black_0))
        }
    }

    fun generateMerchantMarkers(merchantId: String = "") {
        val displayMerchants = AllMerchants.getDisplayMerchants(merchantId)
        if (displayMerchants.isNotEmpty()) {
            addMarkersToMap(AllMerchants.getDisplayMerchants(), merchantId)
        } else {
            mMap.clear()
            merchantListView?.adapter = null
        }
    }

    fun moveMapView(moveY: Float) {
        val layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = (getDefaultMapTopMargin() + moveY).toInt()
        this.layoutParams = layoutParams
    }

    fun getMyLocation() {
        val latLng = LatLng(AllMerchants.myLat.toDouble(), AllMerchants.myLng.toDouble())
        moveCamera(latLng)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val merchantId = mapMarkers[marker] ?: ""
        generateMerchantMarkers(merchantId)
        generateMerchantListView(AllMerchants.getDisplayMerchants(merchantId))
        return true

    }

    fun hideMerchantList() {
        merchantListView?.visibility = View.GONE
        generateMerchantMarkers()
    }


}