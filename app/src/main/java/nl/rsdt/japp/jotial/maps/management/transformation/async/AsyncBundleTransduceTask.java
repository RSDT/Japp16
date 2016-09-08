package nl.rsdt.japp.jotial.maps.management.transformation.async;

import android.os.AsyncTask;
import android.os.Bundle;

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.Transducable;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
public class AsyncBundleTransduceTask extends AsyncTask<Transducable, Integer, Bundle> {

    protected OnBundleTransduceCompletedCallback callback;

    public AsyncBundleTransduceTask(OnBundleTransduceCompletedCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Bundle doInBackground(Transducable... transducables) {
        Bundle buffer = new Bundle();
        AbstractTransducer transducer;
        for(int i = 0; i < transducables.length; i++) {
            transducer = transducables[i].getTransducer();
            if(transducer != null) {
                transducer.setSaveEnabled(false);
                transducer.transduceToBundle(buffer);
            }

        }
        return buffer;
    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        super.onPostExecute(bundle);
        if(callback != null) {
            callback.onTransduceCompleted(bundle);
        }
    }

    public interface OnBundleTransduceCompletedCallback {
        void onTransduceCompleted(Bundle bundle);
    }

}
