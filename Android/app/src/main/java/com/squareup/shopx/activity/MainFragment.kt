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
import com.google.android.gms.maps.OnMapReadyCallback
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
import com.squareup.shopx.widget.BottomSheetRL
import com.squareup.shopx.widget.MapMaskCL
import com.squareup.shopx.widget.customizedseekbar.IndicatorSeekBar
import com.squareup.shopx.widget.customizedseekbar.OnSeekChangeListener
import com.squareup.shopx.widget.customizedseekbar.SeekParams
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


class MainFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "MainFragment"

    private lateinit var mMap: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var floatingMarkersOverlay: FloatingMarkerTitlesOverlay? = null
    private var merchantListView: RecyclerView? = null
    private var bottomSheet: BottomSheetRL? = null
    private var mapContent: ConstraintLayout? = null

    private var distanceFilterInput: EditText? = null
    private var distanceFilterButton: TextView? = null
    private var onlySeeDiscount: TextView? = null
    private var onlySeeLoyalty: TextView? = null
    private var onlySeeAREnable: TextView? = null
    private val snapHelper = PagerSnapHelper()
    private var mapMaskCL: MapMaskCL? = null

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
        bottomSheet?.mainFragment = this
        val layoutParams = bottomSheet!!.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = getDefaultMapTopMargin()
        bottomSheet?.layoutParams = layoutParams

        mapMaskCL = view.findViewById(R.id.pull_up_cl)


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

        mapContent = view.findViewById(R.id.map_content)
        distanceFilterInput = view.findViewById(R.id.distance_filter_input)
        distanceFilterButton = view.findViewById(R.id.distance_filter)
        onlySeeDiscount = view.findViewById(R.id.only_see_discount)
        onlySeeLoyalty = view.findViewById(R.id.only_see_loyalty)
        onlySeeAREnable = view.findViewById(R.id.only_see_ar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        merchantListView = view.findViewById(R.id.merchant_list)
        merchantListView?.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        snapHelper.attachToRecyclerView(merchantListView)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = SupportMapFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.map_container, mapFragment).commit()
        mapFragment.getMapAsync(this)
        return view
    }

    fun getDefaultMapTopMargin(): Int {
        return UIUtils.getHeight(requireContext()) - UIUtils.dp2px(requireContext(), 124f)
    }

    fun expandMapView() {
        mapMaskCL?.visibility = View.GONE
        val currentParams = bottomSheet!!.layoutParams as ConstraintLayout.LayoutParams
        val animator: ValueAnimator = ValueAnimator.ofInt(currentParams.topMargin, 0)
        animator.duration = 180
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            val layoutParams = bottomSheet!!.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = it.animatedValue as Int
            bottomSheet!!.layoutParams = layoutParams

            if ((it.animatedValue as Int) == 0) {
                mapContent?.visibility = View.VISIBLE
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
            val layoutParams = bottomSheet!!.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = it.animatedValue as Int
            bottomSheet!!.layoutParams = layoutParams

            if ((it.animatedValue as Int) == getDefaultMapTopMargin()) {
                mapContent?.visibility = View.GONE
                bottomSheet?.isExpanded = false
                mapMaskCL?.visibility = View.VISIBLE
            }
        }
        animator.start()
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

        floatingMarkersOverlay = view?.findViewById<FloatingMarkerTitlesOverlay>(R.id.map_floating_markers_overlay)
        floatingMarkersOverlay?.setSource(mMap)

        // Add a marker in Sydney and move the camera

        if (context == null) {
            return
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        fusedLocationClient!!.lastLocation
            .addOnSuccessListener(requireActivity()) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {

                    AllMerchants.myLat = location.latitude.toFloat()
                    AllMerchants.myLng = location.longitude.toFloat()
                    moveCamera(LatLng(location.latitude, location.longitude))
                } else {
                    Toast.makeText(requireContext(), "Loading location failed. Please try later", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun animateCamera(latLng: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15.0f
            )
        )
    }

    fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15.0f
            )
        )
    }

    fun addMarkersToMap(merchants: List<AllMerchantsResponse.ShopXMerchant>, focusedPosition: Int) {
        mMap.clear()
        floatingMarkersOverlay?.clearMarkers()
        for (i in merchants.indices) {
            val id: Long = i.toLong() + 1
            val latLng = LatLng(merchants[i].lat.toDouble(), merchants[i].lng.toDouble())
            val title = merchants[i].businessName
            val mi = MarkerInfo(latLng, title, Color.BLACK)
            mMap.addMarker(
                MarkerOptions().position(mi.coordinates).icon(
                    BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap(
                i == focusedPosition
            ))))
            floatingMarkersOverlay?.addMarker(id, mi)
        }
    }

    private fun createColoredMarkerBitmap(isLarge: Boolean): Bitmap {
        val height = if (isLarge) 120 else 80
        val width = if (isLarge) 120 else 80

        val bitmapdraw: BitmapDrawable =
            resources.getDrawable(R.drawable.marker_normal) as BitmapDrawable
        val b: Bitmap = bitmapdraw.getBitmap()
        return Bitmap.createScaledBitmap(b, width, height, false)
    }



    private fun requestAllMerchants() {
        ShopXApiService.getInstance().getAllMerchants()
            .subscribe(object: Observer<AllMerchantsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AllMerchantsResponse?) {

                    requireActivity().runOnUiThread {
                        if (value?.code == 1) {
                            Toast.makeText( requireActivity(), value.msg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        value?.merchants?.let {
                            if (it.size > 0) {
                                AllMerchants.allMerchants = it
                                generateDisplayMerchants()
                                setFilterListeners()
                            }
                        }
                    }

                }

                override fun onError(e: Throwable?) {
                    requireActivity().runOnUiThread {
                        Toast.makeText( requireActivity(), e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    var currentFocusedMarker = 0;
    fun generateMerchantListView(merchants: List<AllMerchantsResponse.ShopXMerchant>) {


        val merchantAdapter = MerchantListAdapter(merchants, requireActivity())
        merchantListView?.adapter = merchantAdapter

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
                            AllMerchants.getDisplayMerchants()[currentFocusedMarker].lat.toDouble(),
                            AllMerchants.getDisplayMerchants()[currentFocusedMarker].lng.toDouble()
                        )
                        )
                        addMarkersToMap(AllMerchants.getDisplayMerchants(), currentFocusedMarker)
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

    }

    fun setFilterListeners() {

        distanceFilterButton?.setOnClickListener {

            showDistanceFilter()

        }

        onlySeeDiscount?.setOnClickListener {
            AllMerchants.onlySeeDiscount = !AllMerchants.onlySeeDiscount
            generateDisplayMerchants()
        }

        onlySeeLoyalty?.setOnClickListener {
//            AllMerchants.onlySeeLoyalty = !AllMerchants.onlySeeLoyalty
//            generateDisplayMerchants()
            val intent = Intent(requireActivity(), LoyaltyActivity::class.java)
            this@MainFragment.startActivity(intent)
        }

        onlySeeAREnable?.setOnClickListener {
//            AllMerchants.onlySeeAREnable = !AllMerchants.onlySeeAREnable
//            generateDisplayMerchants()
            //createNotification()

            //behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            collapseMapView()
        }
    }

    private fun showDistanceFilter() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.distance_filter_bottom_sheet, null, false)
        bottomSheetDialog.setContentView(dialogView)

        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView!!.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val seekBar = dialogView.findViewById<IndicatorSeekBar>(R.id.distance_filter)
        seekBar.setProgress(AllMerchants.distanceLimit)
        setDistanceFilterTextColor(AllMerchants.distanceLimit, dialogView)

        seekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                setDistanceFilterTextColor(seekBar!!.progress.toFloat(), dialogView)

                AllMerchants.distanceLimit = seekBar.progress.toFloat()
                generateDisplayMerchants()
            }

        }
        bottomSheetDialog.show()
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

    fun generateDisplayMerchants() {
        val displayMerchants = AllMerchants.getDisplayMerchants()
        if (displayMerchants.isNotEmpty()) {
            addMarkersToMap(AllMerchants.getDisplayMerchants(), 0)
            generateMerchantListView(AllMerchants.getDisplayMerchants())
            // move to the first merchant
            moveCamera(
                LatLng(displayMerchants[0].lat.toDouble(),
                displayMerchants[0].lng.toDouble()
            )
            )
        } else {
            mMap.clear()
            merchantListView?.adapter = null
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

    fun moveMapView(moveY: Float) {
        val layoutParams = bottomSheet!!.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = (getDefaultMapTopMargin() + moveY).toInt()
        bottomSheet!!.layoutParams = layoutParams
    }


}