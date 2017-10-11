package nl.rsdt.japp.jotial.maps;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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

public class NavigationLocationManager implements ChildEventListener, ValueEventListener {
    public static final String FDB_NAME = "autos";
    private DatabaseReference reference;
    private OnNewLocation callback;
    private Location oldLoc;

    public NavigationLocationManager() {
        reference = FirebaseDatabase.getInstance().getReference().child(FDB_NAME);
        reference.addValueEventListener(this);
    }
    private void update(final DataSnapshot snapshot, final String child){
        int id = JappPreferences.getAccountId();
        if (id >= 0){
            AutoApi api = Japp.getApi(AutoApi.class);
            api.getInfoById(JappPreferences.getAccountKey(), id).enqueue(new Callback<AutoInzittendeInfo>() {
                @Override
                public void onResponse(Call<AutoInzittendeInfo> call, Response<AutoInzittendeInfo> response) {
                    if (response.code() == 200){
                        AutoInzittendeInfo info = response.body();
                        Location loc = snapshot.child(info.autoEigenaar).getValue(Location.class);
                        if ((oldLoc != null && !oldLoc.equals(loc))||(oldLoc == null && loc != null)){
                            oldLoc = loc;
                            if (callback != null){
                                callback.onNewLocation(loc);
                            }
                        }
                    }else if (response.code() == 404){
                        if (callback != null){
                            callback.onNotInCar();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AutoInzittendeInfo> call, Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        update(dataSnapshot,s);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        update(dataSnapshot,s);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        update(dataSnapshot, dataSnapshot.getKey());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        throw databaseError.toException();
    }

    public void setCallback(OnNewLocation callback) {
        this.callback = callback;
    }

    public interface OnNewLocation{
        void onNewLocation(Location location);
        void onNotInCar();
    }
}
