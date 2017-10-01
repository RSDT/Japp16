package nl.rsdt.japp.jotial.maps;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.firebase.Location;
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.net.apis.AutoApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattijn on 30/09/17.
 */

public class NavigationLocationManager implements ValueEventListener {
    private String autoEigenaar;
    private DatabaseReference reference;
    private OnNewLocation callback;


    public NavigationLocationManager() {
    }


    public void setAutoEigenaar(final String autoEigenaar){
        if (this.autoEigenaar != null) {
            if (this.autoEigenaar.equals(autoEigenaar)) {
                return;
            }
        }else if (autoEigenaar == null){
            return;
        }
        if (this.autoEigenaar != null){
            reference.removeEventListener(this);
            reference = null;
            this.autoEigenaar = null;
        }
        this.autoEigenaar = autoEigenaar;
        if (this.autoEigenaar != null) {
            reference = FirebaseDatabase.getInstance().getReference("auto/" + autoEigenaar);
            reference.addValueEventListener(this);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final Location location = dataSnapshot.getValue(Location.class);
        if (location != null){
            int id = JappPreferences.getAccountId();
            if (id>=0) {
                AutoApi api = Japp.getApi(AutoApi.class);
                final NavigationLocationManager self = this;
                final String autoEigenaar = this.autoEigenaar;
                api.getInfoById(JappPreferences.getAccountKey(),id).enqueue(new Callback<AutoInzittendeInfo>() {
                    @Override
                    public void onResponse(Call<AutoInzittendeInfo> call, Response<AutoInzittendeInfo> response) {
                        if (response.code() == 200){
                            AutoInzittendeInfo info = response.body();
                            if (info != null){
                                if (info.autoEigenaar.equals(autoEigenaar)){
                                    if (callback != null){
                                        callback.onNewLocation(location); //dit mag alleen als de autoEigenaar klopt.
                                    }
                                }else{
                                    if (callback != null) {
                                        callback.onNotInCar(self.autoEigenaar, info.autoEigenaar);
                                    }
                                    self.setAutoEigenaar(info.autoEigenaar);
                                }
                            }
                        }else if (response.code() == 404){
                            if (callback != null) {
                                callback.onNotInCar(self.autoEigenaar, null);
                            }
                            self.setAutoEigenaar(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<AutoInzittendeInfo> call, Throwable t) {

                    }
                });
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void setCallback(OnNewLocation callback) {
        this.callback = callback;
    }

    public interface OnNewLocation{
        void onNewLocation(Location location);
        void onNotInCar(String notInCar, String inCar);
    }
}
