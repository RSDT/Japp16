package nl.rsdt.japp.service.cloud.messaging;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

import nl.rsdt.japp.service.cloud.data.MessageType;
import nl.rsdt.japp.service.cloud.data.NoticeInfo;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-8-2016
 * Handles the messages for Japp
 */
public class MessageManager {

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
    public void onMessage(RemoteMessage message) {
        if(message.getData() != null && !message.getData().isEmpty()) {
            Map<String, String> data = message.getData();
            String type = data.get("type");
            if(type.equals(MessageType.NOTICE)) {
                if(listeners.size() > 0) {
                    NoticeInfo info = NoticeInfo.parse(data);
                    if(info != null) {
                        UpdateMessageListener listener;
                        for(int i = 0; i < listeners.size(); i++) {
                            listener = listeners.get(i);

                            if(listener != null) {
                                listener.onNoticeMessageReceived(info);
                            }
                        }
                    }
                }
            } else if(type.equals(MessageType.NEW) || type.equals(MessageType.UPDATE)) {
                if(listeners.size() > 0)  {
                    UpdateInfo info = UpdateInfo.parse(data);
                    if(info != null && info.type != null && info.action != null) {
                        UpdateMessageListener listener;
                        for(int i = 0; i < listeners.size(); i++) {
                            listener = listeners.get(i);

                            if(listener != null) {
                                listener.onUpdateMessageReceived(info);
                            }
                        }
                    }
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

        void onNoticeMessageReceived(NoticeInfo info);
    }

}
