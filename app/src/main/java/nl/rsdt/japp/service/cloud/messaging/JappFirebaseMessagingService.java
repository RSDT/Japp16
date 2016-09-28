package nl.rsdt.japp.service.cloud.messaging;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class JappFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Japp.getUpdateManager().onMessage(remoteMessage);
    }
}
