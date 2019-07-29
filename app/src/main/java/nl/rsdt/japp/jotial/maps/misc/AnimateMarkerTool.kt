package nl.rsdt.japp.jotial.maps.misc

/* Copyright 2013 Google Inc.
   Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Property
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.gms.maps.model.LatLng
import nl.rsdt.japp.jotial.maps.wrapper.IMarker


object AnimateMarkerTool {
    fun animateMarkerToGB(marker: IMarker, finalPosition: LatLng, latLngInterpolator: LatLngInterpolator, duration: Long) {
        val startPosition = marker.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = duration.toFloat()

        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t: Float = 0.toFloat()
            var v: Float = 0.toFloat()

            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)

                marker.position = latLngInterpolator.interpolate(v, startPosition, finalPosition)

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun animateMarkerToHC(marker: IMarker, finalPosition: LatLng, latLngInterpolator: LatLngInterpolator, duration: Long) {
        val startPosition = marker.position

        val valueAnimator = ValueAnimator()
        valueAnimator.addUpdateListener { animation ->
            val v = animation.animatedFraction
            val newPosition = latLngInterpolator.interpolate(v, startPosition, finalPosition)
            marker.position = newPosition
        }
        valueAnimator.setFloatValues(0, 1) // Ignored.
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun animateMarkerToICS(marker: IMarker, finalPosition: LatLng, latLngInterpolator: LatLngInterpolator, duration: Long) {
        val typeEvaluator = TypeEvaluator<LatLng> { fraction, startValue, endValue -> latLngInterpolator.interpolate(fraction, startValue, endValue) }
        val property = Property.of(IMarker::class.java, LatLng::class.java, "position")
        val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
        animator.duration = duration
        animator.start()
    }
}