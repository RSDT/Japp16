package nl.rsdt.japp.application;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;


public class InzittendenAdapter extends RecyclerView.Adapter<InzittendenAdapter.MyViewHolder> {

    private List<AutoInzittendeInfo> data;

    public InzittendenAdapter() {
        data = new ArrayList<>();
    }

    public void setData(List<AutoInzittendeInfo> newData){
        this.data = newData;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private AutoInzittendeInfo inzittendeInfo = null;
        private TextView v;
        public MyViewHolder(TextView v) {
            super(v);
            this.v = v;
        }

        public void refresh(){
            String text;
            if (inzittendeInfo !=  null){
                text = inzittendeInfo.gebruikersNaam + " " + inzittendeInfo.rol;
            }  else{
                text = Japp.getAppResources().getString(R.string.unkown);
            }
            v.setText(text);
        }


        public void setInzittendeInfo(AutoInzittendeInfo inzittendeInfo) {
            this.inzittendeInfo = inzittendeInfo;
            refresh();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InzittendenAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        final TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.auto_inzittenden_list_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setInzittendeInfo(data.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }
}
