
package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

//import android.location.GnssAntennaInfo
//import android.location.GpsStatus
//import android.net.ConnectivityManager
//import android.net.ConnectivityManager.NetworkCallback
//import android.net.Network
//import android.net.sip.SipAudioCall.Listener
//import com.google.android.gms.tasks.Task
//import kotlinx.coroutines.suspendCancellableCoroutine
//import kotlin.coroutines.resumeWithException
//
//
//
//
//
//
//suspend fun doSomething(): Result {
//    return suspendCancellableCoroutine { continuation ->
//        val id = ConnectivityManager.NetworkCallback() {
//
//
//            override fun onAvailable(network: Network) {
//                super.onAvailable(network)
//                Log.e("TAG", "onAvailable: ", )
//            }
//
//            override fun onLost(network: Network) {
//                super.onLost(network)
//                Log.e("TAG", "onLost: ")
//            }
//
//        }
//
//        continuation.invokeOnCancellation {
//            Library.cancel(id)
//        }
//    }
//}
//
//connectivityManager.registerDefaultNetworkCallback(object :
//    ConnectivityManager.NetworkCallback() {
//    override fun onAvailable(network: Network) {
//        super.onAvailable(network)
//        Log.e("TAG", "onAvailable: ", )
//    }
//
//    override fun onLost(network: Network) {
//        super.onLost(network)
//        Log.e("TAG", "onLost: ")
//    }
//})
//}
