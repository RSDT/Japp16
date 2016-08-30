package nl.rsdt.japp.service.cloud.messaging;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Map;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-8-2016
 * Handles updating for Japp
 */
public class UpdateManager {

    public static final int NOTIFICATION_ID = 32;

    /**
     * The list of listeners.
     * */
    private ArrayList<UpdateMessageListener> listeners = new ArrayList<>();

    /**
     * Adds a listener for when a UpdateMessage is received.
     * */
    public void add(UpdateMessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for when a UpdateMessage is received.
     * */
    public void remove(UpdateMessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets invoked when the service received a message with data.
     * */
    public void onMessageData(Map<String, String> data) {
        UpdateInfo info = UpdateInfo.parse(data);
        if(info != null) {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(Japp.getInstance())
                            .setSmallIcon(R.drawable.ic_sync_black_48dp)
                            .setContentTitle("Update Beschikbaar")
                            .setContentText("Update beschikbaar voor " + info.type);

            NotificationManager mNotifyMgr = (NotificationManager) Japp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());


            UpdateMessageListener listener;
            for(int i = 0; i < listeners.size(); i++) {
                listener = listeners.get(i);

                if(listener != null) {
                    listener.onUpdateMessageReceived(info);
                }
            }
        }
    }

    /**
     * Defines a listener for when a UpdateMessage is received.
     * */
    public interface UpdateMessageListener {

        /**
         * Gets invoked when a UpdateMessage is received.
         *
         * @param info The information about the update.
         * */
        void onUpdateMessageReceived(UpdateInfo info);
    }

}
