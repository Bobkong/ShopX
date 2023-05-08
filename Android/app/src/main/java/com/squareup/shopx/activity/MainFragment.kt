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
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import com.google.ar.core.codelabs.arlocalizer.helpers.GeoPermissionsHelper
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.MerchantListAdapter
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.widget.BottomSheetRL
import com.squareup.shopx.utils.BroadcastReceiverPage
import com.squareup.shopx.widget.MaskRL
import com.squareup.shopx.utils.UIUtils
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
    private var maskRL: MaskRL? = null
    private var onlySeeLoyalty: TextView? = null
    private var onlySeeAREnable: TextView? = null
    private val snapHelper = PagerSnapHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_main, null)
        // The app must have been given the Location permission. If we don't have it yet, request it.
        if (!GeoPermissionsHelper.hasGeoPermissions(requireActivity())) {
            GeoPermissionsHelper.requestPermissions(activity)
            return view
        }
        bottomSheet = view.findViewById(R.id.bottom_sheet)
        bottomSheet?.mainFragment = this
        maskRL = view.findViewById(R.id.mask_rl)
        maskRL?.setOnClickListener {
            Log.i(TAG, "just test")
        }

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

    fun expandMapView() {
        val currentParams = bottomSheet!!.layoutParams as CoordinatorLayout.LayoutParams
        val animator: ValueAnimator = ValueAnimator.ofInt(currentParams.topMargin, 0)
        animator.duration = 180
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            val layoutParams = bottomSheet!!.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.topMargin = it.animatedValue as Int
            bottomSheet!!.layoutParams = layoutParams

            if ((it.animatedValue as Int) == 0) {
                maskRL?.visibility = View.GONE
                mapContent?.visibility = View.VISIBLE
                requestAllMerchants()
            }
        }
        animator.start()
    }

    private fun collapseMapView() {
        val animator: ValueAnimator = ValueAnimator.ofInt(0, UIUtils.dp2px(requireContext(), 500f))
        animator.duration = 180
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            val layoutParams = bottomSheet!!.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.topMargin = it.animatedValue as Int
            bottomSheet!!.layoutParams = layoutParams

            if ((it.animatedValue as Int) == UIUtils.dp2px(requireContext(), 500f)) {
                maskRL?.visibility = View.VISIBLE
                mapContent?.visibility = View.GONE
                bottomSheet?.isExpanded = false
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (!GeoPermissionsHelper.hasGeoPermissions( requireActivity())) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText( requireActivity(), "Camera and location permissions are needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!GeoPermissionsHelper.shouldShowRequestPermissionRationale(requireActivity())) {
                // Permission denied with checking "Do not ask again".
                GeoPermissionsHelper.launchPermissionSettings(requireActivity())
            }
            requireActivity().finish()
        }
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
            AllMerchants.distanceLimit = distanceFilterInput?.text.toString().toFloat()
            generateDisplayMerchants()
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
        val layoutParams = bottomSheet!!.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.topMargin = (UIUtils.dp2px(requireContext(), 500f) + moveY).toInt()
        bottomSheet!!.layoutParams = layoutParams
    }


}