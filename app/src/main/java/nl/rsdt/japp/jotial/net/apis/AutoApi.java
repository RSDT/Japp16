package nl.rsdt.japp.jotial.net.apis;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by mattijn on 30/09/17.
 */

public interface AutoApi {

    @GET("/auto/{key}/info/{id}")
    Call<AutoInzittendeInfo> getInfoById(@Path("key") String key, @Path("id") int id);

    @GET("/auto/{key}/onecar/{name}")
    Call<ArrayList<AutoInzittendeInfo> > getCarByName(@Path("key") String key, @Path("name") int id);


}
