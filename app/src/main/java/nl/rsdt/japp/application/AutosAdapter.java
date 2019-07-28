package nl.rsdt.japp.application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.data.bodies.AutoPostBody;
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;
import nl.rsdt.japp.jotial.net.apis.AutoApi;
import retrofit2.Callback;

public class AutosAdapter extends RecyclerView.Adapter<AutosAdapter.MyViewHolder> {
    private final Callback<Void> callback;
    private Map<String, List<AutoInzittendeInfo>> data;
    private String[] eigenaars = new String[0];

    public AutosAdapter(Callback<Void> callback) {
        data = new HashMap<>();
        this.callback = callback;
    }
    private void setEigenaars(){
        Set<String> eigenaarsSet = data.keySet();

        eigenaars= new String[eigenaarsSet.size()];
        eigenaarsSet.toArray(eigenaars);
        Arrays.sort(eigenaars);
    }
    public void setData(Map<String, List<AutoInzittendeInfo>> newData){
        this.data = newData;
        setEigenaars();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AutosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        final Button v = (Button) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.auto_list_layout, parent, false);
        return new MyViewHolder(v, callback);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Callback<Void> callback;
        // each data item is just a string in this case
        private String eigenaar = "";
        private List<AutoInzittendeInfo> inzittendeInfos = new ArrayList<>();
        private Button button;
        public MyViewHolder(Button v, Callback<Void> callback) {
            super(v);
            this.callback = callback;
            button = v;
            v.setOnClickListener(this);
        }

        public void refresh(){
            String text = eigenaar + ":" + inzittendeInfos.size();
            button.setText(text);
        }
        public void setEigenaar(String eigenaar) {
            this.eigenaar = eigenaar;
            refresh();
        }

        public void setInzittendeInfos(List<AutoInzittendeInfo> inzittendeInfos) {
            this.inzittendeInfos = inzittendeInfos;
            refresh();
        }

        @Override
        public void onClick(View view) {
            Button tv = (Button) view;
            tv.setText(tv.getText());
            AutoApi autoApi = Japp.getApi(AutoApi.class);
            if (tv.getText().toString().contains( Japp.getAppResources().getString(R.string.create_car))){
                AutoPostBody body = AutoPostBody.getDefault();
                body.setAutoEigenaar(JappPreferences.getAccountUsername());
                autoApi.post(body).enqueue(callback);
            } else {
                AutoPostBody body = AutoPostBody.getDefault();
                body.setAutoEigenaar(eigenaar);
                autoApi.post(body).enqueue(callback);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position == 0){
            holder.setEigenaar( Japp.getAppResources().getString(R.string.create_car));
            holder.setInzittendeInfos(new ArrayList<AutoInzittendeInfo>());
            return;
        }
        int i = position - 1;
        holder.setEigenaar(eigenaars[i]);
        holder.setInzittendeInfos(data.get(eigenaars[i]));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size()+1;
    }
}
