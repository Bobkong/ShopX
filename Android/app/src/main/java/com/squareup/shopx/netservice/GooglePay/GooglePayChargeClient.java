package com.squareup.shopx.netservice.GooglePay;

import android.widget.Toast;

import androidx.annotation.Nullable;

import com.squareup.shopx.activity.OrderActivity;

import sqip.Call;
import sqip.GooglePay;
import sqip.GooglePayNonceResult;

public class GooglePayChargeClient {

  @Nullable
  private OrderActivity activity;
  @Nullable
  private Call<GooglePayNonceResult> requestNonceCall;
  @Nullable
  private Call<ChargeResult> chargeCall;

  public GooglePayChargeClient() {
  }

  public void charge(String googlePayToken) {
    if (nonceRequestInFlight() || chargeRequestInFlight()) {
      return;
    }
    requestNonceCall = GooglePay.requestGooglePayNonce(googlePayToken);
    requestNonceCall.enqueue(result -> onNonceRequestResult(googlePayToken, result));
  }

  private void onNonceRequestResult(String googlePayToken, GooglePayNonceResult result) {
    if (!nonceRequestInFlight()) {
      return;
    }
    requestNonceCall = null;
    if (activity == null) {
      return;
    }
    if (result.isSuccess()) {
      String nonce = result.getSuccessValue().getNonce();
      ((OrderActivity)activity).payOrder(nonce);
      // request square
    } else if (result.isError()) {
      GooglePayNonceResult.Error error = result.getErrorValue();
      switch (error.getCode()) {
        case NO_NETWORK:
        case UNSUPPORTED_SDK_VERSION:
        case USAGE_ERROR:
          activity.runOnUiThread(() -> {
            Toast.makeText(activity, "Error happens. Try it later!", Toast.LENGTH_SHORT).show();
          });
          break;
      }
    }
  }

  public void cancel() {
    if (nonceRequestInFlight()) {
      requestNonceCall.cancel();
      requestNonceCall = null;
    }
    if (chargeRequestInFlight()) {
      chargeCall.cancel();
      chargeCall = null;
    }
  }

  public void onActivityCreated(OrderActivity activity) {
    this.activity = activity;
  }

  public void onActivityDestroyed() {
    activity = null;
  }

  private boolean chargeRequestInFlight() {
    return chargeCall != null;
  }

  private boolean nonceRequestInFlight() {
    return requestNonceCall != null;
  }

}
