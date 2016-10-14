package nl.rsdt.japp.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.data.structures.area348.VosStatusInfo;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    public void refresh() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jotihunt.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VosApi api = retrofit.create(VosApi.class);
        api.getStatus().enqueue(this);
    }

    @Override
    public void onResponse(Call<VosStatusInfo> call, Response<VosStatusInfo> response) {
        VosStatusInfo info = response.body();
        if(info != null) {
            for(VosStatusInfo.Status status : info.getStatus()) {
                View view = getView();
                LinearLayout layout = null;
                switch (status.getTeam()) {
                    case "Alpha":
                        layout = (LinearLayout) view.findViewById(R.id.fragment_home_alpha);
                        break;
                    case "Bravo":
                        layout = (LinearLayout) view.findViewById(R.id.fragment_home_bravo);
                        break;
                    case "Charlie":
                        layout = (LinearLayout) view.findViewById(R.id.fragment_home_charlie);
                        break;
                    case "Delta":
                        layout = (LinearLayout) view.findViewById(R.id.fragment_home_delta);
                        break;
                    case "Echo":
                        layout = (LinearLayout) view.findViewById(R.id.fragment_home_echo);
                        break;
                    case "Foxtrot":
                        layout = (LinearLayout) view.findViewById(R.id.fragment_home_foxtrot);
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

    @Override
    public void onFailure(Call<VosStatusInfo> call, Throwable t) {
        Log.e(TAG, t.toString(), t);
    }
}
