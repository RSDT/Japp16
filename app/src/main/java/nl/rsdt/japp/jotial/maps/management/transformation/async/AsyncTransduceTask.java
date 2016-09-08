package nl.rsdt.japp.jotial.maps.management.transformation.async;

import android.os.AsyncTask;

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
public class AsyncTransduceTask<I, O extends AbstractTransducer.Result> extends AsyncTask<Integer, Integer, O> {

    protected AbstractTransducer<I, O> transducer;

    protected I data;

    protected OnTransduceCompletedCallback<O> callback;

    public void setTransducer(AbstractTransducer<I, O> transducer) {
        this.transducer = transducer;
    }

    public void setData(I data) {
        this.data = data;
    }

    public void setCallback(OnTransduceCompletedCallback<O> callback) {
        this.callback = callback;
    }

    @Override
    protected O doInBackground(Integer... integers) {
        return transducer.generate(data);
    }

    @Override
    protected void onPostExecute(O output) {
        if(output != null && callback != null) {
            callback.onTransduceCompleted(output);
        }
    }

    public interface OnTransduceCompletedCallback<O extends AbstractTransducer.Result> {
        void onTransduceCompleted(O result);
    }
}
