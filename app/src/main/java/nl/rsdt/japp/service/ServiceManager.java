package nl.rsdt.japp.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
public class ServiceManager<S extends Service, B extends Binder> implements ServiceConnection {

    protected Class<S> type;

    protected boolean bound = false;

    public boolean isBound() {
        return bound;
    }

    public ServiceManager(Class<S> service) {
        type = service;
    }

    protected ArrayList<OnBindCallback<B>> callbacks = new ArrayList<>();

    public void add(OnBindCallback<B> callback) {
        this.callbacks.add(callback);
    }

    public void remove(OnBindCallback<B> callback) {
        this.callbacks.remove(callback);
    }

    public void bind(Context context) {
        Intent intent = new Intent(context, type);
        context.startService(intent);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context) {
        context.unbindService(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        bound = true;
        OnBindCallback<B> callback;
        for(int i = 0; i < callbacks.size(); i++) {
            callback = callbacks.get(i);
            if(callback != null) {
                callback.onBind((B)iBinder);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public interface OnBindCallback<B extends Binder> {
        void onBind(B binder);
    }
}
