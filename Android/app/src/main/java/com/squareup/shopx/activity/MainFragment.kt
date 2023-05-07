package com.squareup.shopx.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.SupportMapFragment
import com.squareup.shopx.R


class MainFragment : Fragment(){

    private val TAG = "MainFragment"
    private var mapFragment: MapFragment? = null
    private var introFragment: IntroFragment? = null
    public var currentFragment = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_main, null)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        introFragment = IntroFragment()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.add(R.id.fragment_container, introFragment!!).commit()
        return view
    }

    public fun changeToMap() {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (mapFragment == null) {
            mapFragment = MapFragment()
            ft.add(R.id.fragment_container, mapFragment!!)
        }
        // mCurrFragment 存储当前显示的 Fragment
        if (introFragment != null) {
            ft.hide(introFragment!!)
        }
        ft.show(mapFragment!!)
        ft.commit()
        currentFragment = 1
    }

    public fun changeToIntro() {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (introFragment == null) {
            introFragment = IntroFragment()
            ft.add(R.id.fragment_container, introFragment!!)
        }
        if (mapFragment != null) {
            ft.hide(mapFragment!!)
        }
        ft.show(introFragment!!)
        ft.commit()
        currentFragment = 0
    }




}