package nl.rsdt.japp.application.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.data.structures.area348.VosStatusInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.net.apis.official.VosApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 9-7-2016
 * Description...
 */
public class HomeFragment extends Fragment implements Callback<VosStatusInfo> {

    public static final String TAG = "HomeFragment";

    public static final String STORAGE_KEY = "VosStatusInfo";

    public static final String GROUP_KEY = "StatusUpdate";

    public static final String VOSSTATUSCHANNEL = "vos_status";

    public static final int NOTIFICATION_ID = 275;

    protected VosStatusInfo lastStatusInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lastStatusInfo = AppData.getObject(STORAGE_KEY, VosStatusInfo.class);
        if(lastStatusInfo != null) {
            updateView(lastStatusInfo);
        }
        refresh();
    }

    public void refresh() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.jotihunt_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VosApi api = retrofit.create(VosApi.class);
        api.getStatus().enqueue(this);
    }

    @Override
    public void onResponse(Call<VosStatusInfo> call, Response<VosStatusInfo> response) {
        VosStatusInfo info = response.body();
        if(info != null) {
            updateView(info);
            if(lastStatusInfo != null) {
                for(VosStatusInfo.Status status : info.getStatus()) {
                    for(VosStatusInfo.Status oldStatus : lastStatusInfo.getStatus()) {
                        if(status.getTeam().equals(oldStatus.getTeam())) {
                            if(status.getStatus() != oldStatus.getStatus()) {
                                showNotification(status);
                            }
                        }
                    }
                }
            } else {
                showNotification(info);
            }

            lastStatusInfo = info;
            AppData.saveObjectAsJsonInBackground(info, STORAGE_KEY);
        }
    }

    public void updateView(VosStatusInfo info) {
        for(VosStatusInfo.Status status : info.getStatus()) {
            View view = getView();
            if(view != null) {
                LinearLayout layout = null;
                switch (status.getTeam()) {
                    case "Alpha":
                        layout = view.findViewById(R.id.fragment_home_alpha);
                        break;
                    case "Bravo":
                        layout = view.findViewById(R.id.fragment_home_bravo);
                        break;
                    case "Charlie":
                        layout = view.findViewById(R.id.fragment_home_charlie);
                        break;
                    case "Delta":
                        layout = view.findViewById(R.id.fragment_home_delta);
                        break;
                    case "Echo":
                        layout = view.findViewById(R.id.fragment_home_echo);
                        break;
                    case "Foxtrot":
                        layout = view.findViewById(R.id.fragment_home_foxtrot);
                        break;
                }
                if(layout != null) {
                    switch (status.getStatus()) {
                        case RED:
                            layout.setBackgroundColor(ContextCompat.getColor(this.getActivity(), android.R.color.holo_red_light));
                            break;
                        case ORANGE:
                            layout.setBackgroundColor(ContextCompat.getColor(this.getActivity(), android.R.color.holo_orange_light));
                            break;
                        case GREEN:
                            layout.setBackgroundColor(ContextCompat.getColor(this.getActivity(), android.R.color.holo_green_light));
                            break;
                    }
                }
            }
        }
    }

    public void showNotification(VosStatusInfo info) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        for(VosStatusInfo.Status status : info.getStatus()) {
            style.addLine(status.getTeam() + " : " + status.getStatus());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getActivity(), VOSSTATUSCHANNEL);
        builder.setContentTitle(getString(R.string.vos_update_title))
                .setContentText(getString(R.string.vos_update_body))
                .setSmallIcon(R.drawable.fox3)
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Activity activity = this.getActivity();
        if (activity != null) {
            NotificationManager mNotifyMgr = (NotificationManager) this.getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public void showNotification(VosStatusInfo.Status status) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getActivity(), VOSSTATUSCHANNEL);
        switch (status.getStatus()){
            case RED:
                builder.setContentTitle(getString(R.string.team_status_update, status.getTeam()))
                        .setContentText(getString(R.string.connot_hunt_team, status.getTeam()))
                        .setColor(Color.rgb(244, 66, 66));
                break;
            case ORANGE:
                builder.setContentTitle(getString(R.string.team_status_update, status.getTeam()))
                        .setContentText(getString(R.string.orange_message, status.getTeam()))
                        .setColor(Color.rgb(214, 118, 8));
                break;
            case GREEN:
                builder.setContentTitle(getString(R.string.team_status_update, status.getTeam()))
                        .setContentText(getString(R.string.groen_message, status.getTeam()))
                        .setColor(Color.rgb(113, 244, 66));
                break;
        }
        builder.setGroup(GROUP_KEY);
        Notification notification = builder.setSmallIcon(R.drawable.fox3)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        NotificationManager mNotifyMgr = (NotificationManager) this.getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onFailure(Call<VosStatusInfo> call, Throwable t) {
        Log.e(TAG, t.toString(), t);
    }


}
