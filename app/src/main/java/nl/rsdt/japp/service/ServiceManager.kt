package nl.rsdt.japp.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
class ServiceManager<S : Service, B : Binder>(protected var type: Class<S>) : ServiceConnection {

    var isBound = false
        protected set

    protected var callbacks = ArrayList<OnBindCallback<B>>()

    fun add(callback: OnBindCallback<B>) {
        this.callbacks.add(callback)
    }

    fun remove(callback: OnBindCallback<B>) {
        this.callbacks.remove(callback)
    }

    fun bind(context: Context) {
        val intent = Intent(context, type)
        context.startService(intent)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun unbind(context: Context) {
        context.unbindService(this)
    }

    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
        isBound = true
        var callback: OnBindCallback<B>?
        for (i in callbacks.indices) {
            callback = callbacks[i]
            callback.onBind(iBinder as B)
        }
    }

    override fun onServiceDisconnected(componentName: ComponentName) {

    }

    interface OnBindCallback<B : Binder> {
        fun onBind(binder: B)
    }
}
