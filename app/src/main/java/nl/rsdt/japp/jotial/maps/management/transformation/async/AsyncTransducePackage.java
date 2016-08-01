package nl.rsdt.japp.jotial.maps.management.transformation.async;

import java.lang.ref.WeakReference;

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;
import nl.rsdt.japp.jotial.maps.management.transformation.TransduceMode;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public final class AsyncTransducePackage<T> {


    private AsyncTransducePackage() { };

    private String data;

    public String getData() {
        return data;
    }

    private String mode = TransduceMode.DATA_MODE;

    public String getMode() {
        return mode;
    }

    private WeakReference<OnTransduceCompletedCallback<T>> callback;

    public OnTransduceCompletedCallback<T> getCallback() {
        return callback.get();
    }

    private AbstractTransducer<T> transducer;

    public AbstractTransducer<T> getTransducer() {
        return transducer;
    }

    private AbstractTransducerResult<T> result;

    public AbstractTransducerResult<T> getResult() {
        return result;
    }

    public void setResult(AbstractTransducerResult<T> result) {
        this.result = result;
    }

    public static class Builder<T> {

        AsyncTransducePackage<T> input = new AsyncTransducePackage<>();

        public Builder<T> setData(String data) {
            input.data = data;
            return this;
        }

        public Builder<T> setMode(String mode) {
            input.mode = mode;
            return this;
        }

        public Builder<T> setCallback(OnTransduceCompletedCallback<T> callback) {
            input.callback = new WeakReference<>(callback);
            return this;
        }

        public Builder<T> setTransducer(AbstractTransducer<T> transducer) {
            input.transducer = transducer;
            return this;
        }

        public AsyncTransducePackage<T> create() {
            return input;
        }
    }
}
