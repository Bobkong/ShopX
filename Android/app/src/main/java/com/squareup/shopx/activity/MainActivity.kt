package com.squareup.shopx.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.exlyo.gmfmt.FloatingMarkerTitlesOverlay
import com.exlyo.gmfmt.MarkerInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.ar.core.codelabs.arlocalizer.helpers.GeoPermissionsHelper
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.MerchantListAdapter
import com.squareup.shopx.model.AllMerchantsResponse
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.w3c.dom.Text


class MainActivity : AppCompatActivity(), OnMapReadyCallback{

    private val TAG = "MainActivity"
    private lateinit var mMap: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var floatingMarkersOverlay: FloatingMarkerTitlesOverlay? = null
    private var merchantListView: RecyclerView? = null

    private var distanceFilterInput: EditText? = null
    private var distanceFilterButton: TextView? = null
    private var onlySeeDiscount: TextView? = null
    private var onlySeeLoyalty: TextView? = null
    private var onlySeeAREnable: TextView? = null
    private val snapHelper = PagerSnapHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.squareup.shopx.R.layout.activity_main)

        // The app must have been given the Location permission. If we don't have it yet, request it.
        if (!GeoPermissionsHelper.hasGeoPermissions(this)) {
            GeoPermissionsHelper.requestPermissions(this)
            return
        }

        distanceFilterInput = findViewById(R.id.distance_filter_input)
        distanceFilterButton = findViewById(R.id.distance_filter)
        onlySeeDiscount = findViewById(R.id.only_see_discount)
        onlySeeLoyalty = findViewById(R.id.only_see_loyalty)
        onlySeeAREnable = findViewById(R.id.only_see_ar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        merchantListView = findViewById(R.id.merchant_list)
        merchantListView?.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        snapHelper.attachToRecyclerView(merchantListView)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.squareup.shopx.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        floatingMarkersOverlay = findViewById<FloatingMarkerTitlesOverlay>(R.id.map_floating_markers_overlay)
        floatingMarkersOverlay?.setSource(mMap)

        // Add a marker in Sydney and move the camera

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        fusedLocationClient!!.lastLocation
            .addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {

                    AllMerchants.myLat = location.latitude.toFloat()
                    AllMerchants.myLng = location.longitude.toFloat()
                    requestAllMerchants()
                } else {
                    Toast.makeText(this@MainActivity, "Loading location failed. Please try later", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun animateCamera(latLng: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 13.0f
            )
        )
    }

    fun addMarkersToMap(merchants: List<ShopXMerchant>, focusedPosition: Int) {
        mMap.clear()
        floatingMarkersOverlay?.clearMarkers()
        for (i in merchants.indices) {
            val id: Long = i.toLong()
            val latLng = LatLng(merchants[i].lat.toDouble(), merchants[i].lng.toDouble())
            val title = merchants[i].businessName
            val mi = MarkerInfo(latLng, title, Color.BLACK)
            mMap.addMarker(MarkerOptions().position(mi.coordinates).icon(BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap(
                i == focusedPosition
            ))))
            floatingMarkersOverlay?.addMarker(id, mi)
            Log.i(TAG, floatingMarkersOverlay.toString())
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
        if (!GeoPermissionsHelper.hasGeoPermissions(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(this, "Camera and location permissions are needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!GeoPermissionsHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                GeoPermissionsHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    private fun requestAllMerchants() {
        ShopXApiService.getInstance().getAllMerchants()
            .subscribe(object: Observer<AllMerchantsResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: AllMerchantsResponse?) {
                    value?.merchants?.let {
                        if (it.size > 0) {
                            AllMerchants.allMerchants = it
                            runOnUiThread {
                                generateDisplayMerchants()
                                setFilterListeners()

                            }
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }

    var currentFocusedMarker = 0;
    fun generateMerchantListView(merchants: List<ShopXMerchant>) {


        val merchantAdapter = MerchantListAdapter(merchants, this)
        merchantListView?.adapter = merchantAdapter

        merchantListView?.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val itemView = snapHelper.findSnapView (recyclerView.layoutManager)
                    var position = -1
                    if (itemView != null) {
                        position = recyclerView.getChildAdapterPosition(itemView)
                    }
                    if (position != currentFocusedMarker && position < AllMerchants.getDisplayMerchants().size - 1) {
                        Log.i(TAG, position.toString())
                        currentFocusedMarker = position
                        animateCamera(LatLng(
                            AllMerchants.getDisplayMerchants()[currentFocusedMarker].lat.toDouble(),
                            AllMerchants.getDisplayMerchants()[currentFocusedMarker].lng.toDouble()
                        ))
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
            AllMerchants.onlySeeLoyalty = !AllMerchants.onlySeeLoyalty
            generateDisplayMerchants()
        }

        onlySeeAREnable?.setOnClickListener {
            AllMerchants.onlySeeAREnable = !AllMerchants.onlySeeAREnable
            generateDisplayMerchants()
        }
    }

    fun generateDisplayMerchants() {
        val displayMerchants = AllMerchants.getDisplayMerchants()
        if (displayMerchants.isNotEmpty()) {
            addMarkersToMap(AllMerchants.getDisplayMerchants(), 0)
            generateMerchantListView(AllMerchants.getDisplayMerchants())
            // move to the first merchant
            animateCamera(LatLng(displayMerchants[0].lat.toDouble(),
                displayMerchants[0].lng.toDouble()
            ))
        } else {
            mMap.clear()
            merchantListView?.adapter = null
        }
    }



}