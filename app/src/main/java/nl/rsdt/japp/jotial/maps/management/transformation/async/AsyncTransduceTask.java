package nl.rsdt.japp.jotial.maps.management.transformation.async;

import android.os.AsyncTask;

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.TransduceMode;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class AsyncTransduceTask extends AsyncTask<AsyncTransducePackage, Integer, AsyncTransducePackage[]> {

    @Override
    protected AsyncTransducePackage[] doInBackground(AsyncTransducePackage... inputs) {
        AsyncTransducePackage input;

        for(int i = 0; i < inputs.length; i++) {
            input = inputs[i];

            if(input != null) {
                AbstractTransducer transducer = input.getTransducer();
                if(transducer != null) {

                    switch (input.getMode())
                    {
                        case TransduceMode.DATA_MODE:
                            String data = input.getData();
                            if(data != null && !data.isEmpty()) {
                                input.setResult(transducer.generate(transducer.extract(data)));
                            }
                            break;
                        case TransduceMode.STORAGE_MODE:
                            input.setResult(transducer.generate(transducer.retrieveFromStorage()));
                            break;
                    }
                }
            }
        }
        return inputs;
    }

    @Override
    protected void onPostExecute(AsyncTransducePackage[] packages) {
        AsyncTransducePackage output;
        for(int i = 0; i < packages.length; i++) {
            output = packages[i];

            OnTransduceCompletedCallback callback = output.getCallback();
            if(callback != null) {
                callback.onTransduceCompleted(output.getResult());
            }
        }
    }
}
