package nl.rsdt.japp.jotial.maps.management.transformation;

import java.lang.reflect.Array;
import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransducePackage;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask;
import nl.rsdt.japp.jotial.maps.management.transformation.async.OnTransduceCompletedCallback;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class AbstractTransducer<T> {

    protected boolean saveEnabled = true;

    public boolean isSaveEnabled() {
        return saveEnabled;
    }

    public void setSaveEnabled(boolean saveEnabled) {
        this.saveEnabled = saveEnabled;
    }

    public abstract T[] retrieveFromStorage();

    public abstract T[] extract(String data);

    public abstract AbstractTransducerResult<T> generate(T[] items);

    public AbstractTransducerResult<T> generate(ArrayList<T> items, Class<T> type) {
        @SuppressWarnings("unchecked")
        T[] array = (T[])Array.newInstance(type, items.size());
        return generate(array);
    }

    public void executeAsync(String data, OnTransduceCompletedCallback<T> callback) {
        new AsyncTransduceTask().execute(
                new AsyncTransducePackage.Builder<T>()
                        .setData(data)
                        .setCallback(callback)
                        .setTransducer(this)
                        .create()
        );
    }


}
