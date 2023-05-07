/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.shopx.map

import com.google.android.gms.maps.GoogleMap
import com.squareup.shopx.activity.MainFragment


open class MapView(val activity: MainFragment, val googleMap: GoogleMap) {

  var setInitialCameraPosition = false
  var cameraIdle = true

  init {
    googleMap.uiSettings.apply {
      isMapToolbarEnabled = false
      isIndoorLevelPickerEnabled = false
      isZoomControlsEnabled = true
      isTiltGesturesEnabled = true
      isScrollGesturesEnabled = true
    }

    googleMap.setOnMarkerClickListener { unused -> false }

    // Add listeners to keep track of when the GoogleMap camera is moving.
    googleMap.setOnCameraMoveListener { cameraIdle = false }
    googleMap.setOnCameraIdleListener { cameraIdle = true }

  }

}