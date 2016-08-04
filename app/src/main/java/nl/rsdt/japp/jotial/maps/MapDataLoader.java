package nl.rsdt.japp.jotial.maps;

import com.google.firebase.crash.FirebaseCrash;

import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.TransduceMode;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransducePackage;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask;
import nl.rsdt.japp.jotial.maps.management.transformation.async.OnTransduceCompletedCallback;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class MapDataLoader {

    private int numOfControllers = 0;

    public int getNumOfControllers() {
        return numOfControllers;
    }

    public void load(OnTransduceCompletedCallback callback) {
        try
        {
            int pCount = 0;
            MapItemController[] controllers = MapItemController.getAll();
            numOfControllers = controllers.length;
            AsyncTransducePackage[] packages = new AsyncTransducePackage[controllers.length];
            MapItemController controller;
            for(int i = 0; i < controllers.length; i++) {
                controller = controllers[i];

                if(controller != null) {
                    packages[pCount] =  new AsyncTransducePackage.Builder<>()
                            .setMode(TransduceMode.STORAGE_MODE)
                            .setTransducer(controller.getTransducer())
                            .setCallback(callback)
                            .create();
                    pCount++;
                }
            }
            new AsyncTransduceTask().execute(packages);
        }
        catch(Exception ex)
        {
            FirebaseCrash.report(ex);
        }
    }

}
