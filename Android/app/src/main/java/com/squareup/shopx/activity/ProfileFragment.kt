package com.squareup.shopx.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.VisibleRegion
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.adapter.LoyaltyCardAdapter
import com.squareup.shopx.adapter.OrderListAdapter
import com.squareup.shopx.model.CartUpdateEvent
import com.squareup.shopx.model.GeneralResponse
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse
import com.squareup.shopx.model.GetAllRecordsRequest
import com.squareup.shopx.model.GetOrdersResponse
import com.squareup.shopx.model.LoyaltyCardsLoadingViewEvent
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService
import com.squareup.shopx.utils.PreferenceUtils
import com.squareup.shopx.widget.CustomDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"
    private lateinit var userName: TextView
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var logOut: ConstraintLayout
    private lateinit var userFullName: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_profile, null)
        userName = view.findViewById(R.id.user_name)
        notificationSwitch = view.findViewById(R.id.notification_switch)
        logOut = view.findViewById(R.id.logout)
        userFullName = view.findViewById(R.id.user_full_name)

        userFullName.text = PreferenceUtils.getUsername()

        var nameString = if (PreferenceUtils.getUsername().length > 1) {
            PreferenceUtils.getUsername().substring(0, 2)
        } else {
            PreferenceUtils.getUsername().substring(0, 1)
        }
        nameString = nameString.toUpperCase()
        userName.text = nameString

        logOut.setOnClickListener {
            showLogoutDialog()
        }

        notificationSwitch.isChecked = PreferenceUtils.getNotification()
        notificationSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (!b) {
                showTurnOffNotificationDialog()
            } else {
                updateNotify(true)
                showTurnOnNotificationDialog()
            }
        }
        return view

    }

    private fun showTurnOffNotificationDialog() {
        val customDialog = CustomDialog(requireContext(), R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        rightAction.text = "Yes"
        leftActivity.text = "Cancel"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "Are you sure you want to stop receiving discounts and loyalty rewards notifications?"
        customDialog.show()

        leftActivity.setOnClickListener {
            customDialog.dismiss()
            notificationSwitch.isChecked = true
        }

        rightAction.setOnClickListener {
            notificationSwitch.isChecked = false
            updateNotify(false)
            customDialog.dismiss()
        }
    }

    private fun showTurnOnNotificationDialog() {
        val customDialog = CustomDialog(requireContext(), R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        rightAction.text = "OK"
        leftActivity.visibility = View.GONE
        dialogTitle.text = "Reminder"
        dialogDesc.text = "Congratulations! Now you can receive notifications of discounts and loyalty rewards nearby."
        customDialog.show()

        rightAction.setOnClickListener {
            customDialog.dismiss()
        }
    }

    private fun showLogoutDialog() {
        val customDialog = CustomDialog(requireContext(), R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        rightAction.text = "Yes"
        leftActivity.text = "Cancel"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "Are you sure you want to log out?"
        customDialog.show()

        leftActivity.setOnClickListener {
            customDialog.dismiss()
        }

        rightAction.setOnClickListener {
            PreferenceUtils.setNotification(true)
            PreferenceUtils.setUsername("")
            PreferenceUtils.setUserPhone("")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun updateNotify(enableNotify: Boolean) {
        PreferenceUtils.setNotification(enableNotify)
        ShopXApiService.getInstance().updateNotify(PreferenceUtils.getUserPhone(), if (enableNotify) 1 else 0)
            .subscribe(object: Observer<GeneralResponse> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: GeneralResponse?) {
                    // do nothing
                }

                override fun onError(e: Throwable?) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Update failed! please try again later", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

            })
    }


}