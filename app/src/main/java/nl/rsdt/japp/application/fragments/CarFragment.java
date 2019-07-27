package nl.rsdt.japp.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.InzittendenAdapter;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.AutosAdapter;
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;
import nl.rsdt.japp.jotial.data.structures.area348.DeletedInfo;
import nl.rsdt.japp.jotial.net.apis.AutoApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CarFragment extends Fragment implements Callback<HashMap<String, List<AutoInzittendeInfo>>> {
    public static final String TAG = "CarFragment";
    private RecyclerView autosRecyclerView;
    private RecyclerView inzittendenRecyclerView;
    private LinearLayout inzittendeLayout;
    private AutosAdapter autosAdapter;
    private InzittendenAdapter inzittendenAdapter;
    private Button stapUitButton;


    public CarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tmp_car, container, false);
        // Inflate the layout for this fragment
        autosRecyclerView = (RecyclerView) v.findViewById(R.id.autos_recycler_view);
        inzittendeLayout = (LinearLayout) v.findViewById(R.id.inzittenden_linear_view);
        inzittendenRecyclerView = (RecyclerView) v.findViewById(R.id.inzittenden_recycler_view);
        stapUitButton = (Button) v.findViewById(R.id.stap_uit_button);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        autosRecyclerView.setHasFixedSize(true);
        inzittendenRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager inzittendeLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager autoLayoutManager = new LinearLayoutManager(getActivity());

        autosRecyclerView.setLayoutManager(autoLayoutManager);
        inzittendenRecyclerView.setLayoutManager(inzittendeLayoutManager);

        // specify an adapter (see also next example)
        autosAdapter = new AutosAdapter(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                refresh();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                refresh();
            }
        });
        inzittendenAdapter = new InzittendenAdapter();
        autosRecyclerView.setAdapter(autosAdapter);
        inzittendenRecyclerView.setAdapter(inzittendenAdapter);
        stapUitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AutoApi autoApi = Japp.getApi(AutoApi.class);
                autoApi.deleteFromCarByName(JappPreferences.getAccountKey(),JappPreferences.getAccountUsername()).enqueue(new Callback<DeletedInfo>() {
                    @Override
                    public void onResponse(Call<DeletedInfo> call, Response<DeletedInfo> response) {
                        refresh();
                    }

                    @Override
                    public void onFailure(Call<DeletedInfo> call, Throwable t) {
                        refresh();
                    }
                });
            }
        });
        refresh();
        return v;
    }

    private List<AutoInzittendeInfo> findInzittendeInfo(Map<String, List<AutoInzittendeInfo>> autos){
        for (List<AutoInzittendeInfo> auto : autos.values()){
            for (AutoInzittendeInfo inzittende : auto){
                if (inzittende.gebruikersNaam.equals(JappPreferences.getAccountUsername())){
                    return auto;
                }
            }
        }
        return null;
    }

    public void refresh() {
        final AutoApi autoApi = Japp.getApi(AutoApi.class);
        autoApi.getAllCars(JappPreferences.getAccountKey()).enqueue(this);
    }

    @Override
    public void onResponse(Call<HashMap<String, List<AutoInzittendeInfo>>> call, Response<HashMap<String, List<AutoInzittendeInfo>>> response) {
        HashMap<String, List<AutoInzittendeInfo>> autos = response.body();
        if (autos == null){
            autos = new HashMap<>();
        }
        List<AutoInzittendeInfo> auto = findInzittendeInfo(autos);
        if (auto == null){
            autosAdapter.setData(autos);
            autosAdapter.notifyDataSetChanged();
            autosRecyclerView.setVisibility(View.VISIBLE);
            inzittendeLayout.setVisibility(View.GONE);
        }else{
            if(auto.get(0).autoEigenaar.equals(JappPreferences.getAccountUsername())){
                stapUitButton.setText(R.string.remove_from_car);
            }else{
                stapUitButton.setText(R.string.get_out_of_car);
            }
            inzittendenAdapter.setData(auto);
            inzittendenAdapter.notifyDataSetChanged();
            autosRecyclerView.setVisibility(View.GONE);
            inzittendeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(Call<HashMap<String, List<AutoInzittendeInfo>>> call, Throwable t) {

    }
}
