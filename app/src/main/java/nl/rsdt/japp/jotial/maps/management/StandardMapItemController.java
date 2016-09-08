package nl.rsdt.japp.jotial.maps.management;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
public abstract class StandardMapItemController<I extends Parcelable, O extends AbstractTransducer.StandardResult<I>> extends MapItemController<ArrayList<I>, O> {

    protected ArrayList<I> items = new ArrayList<>();

    @Override
    public void onIntentCreate(Bundle bundle) {
        super.onIntentCreate(bundle);
        if(bundle != null) {
            O result = bundle.getParcelable(getBundleId());
            if(result != null) {
                this.items = result.getItems();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(getBundleId()))
            {
                this.items = savedInstanceState.getParcelableArrayList(getBundleId());
                O result = getTransducer().generate(items);
                if(googleMap != null) {
                    processResult(result);
                }
                else {
                    buffer = result;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putParcelableArrayList(getBundleId(), items);
    }

    @Override
    public void merge(O other) {
        clear();
        processResult(other);
    }

    @Override
    protected void processResult(O result) {
        super.processResult(result);
        this.items.addAll(result.getItems());
    }

    @Override
    protected void clear() {
        super.clear();
        items.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(items != null)
        {
            /**
             * Do not clear the list or the parcelable list inside the bundle will be empty as well
             * */
            items = null;
        }
    }
}
