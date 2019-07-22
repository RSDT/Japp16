package nl.rsdt.japp.jotial.net.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.rsdt.japp.jotial.data.bodies.AutoPostBody;
import nl.rsdt.japp.jotial.data.bodies.AutoUpdateTaakPostBody;
import nl.rsdt.japp.jotial.data.structures.area348.AutoEigenaarInfo;
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;
import nl.rsdt.japp.jotial.data.structures.area348.DeletedInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by mattijn on 30/09/17.
 */

public interface AutoApi {

    @GET("/auto/{key}/info/{id}")
    Call<AutoInzittendeInfo> getInfoById(@Path("key") String key, @Path("id") int id);

    @GET("/auto/{key}/onecar/{id}")
    Call<ArrayList<AutoInzittendeInfo>> getCarById(@Path("key") String key, @Path("id") int id);

    @GET("/auto/{key}/onecar/{name}")
    Call<ArrayList<AutoInzittendeInfo>> getCarByName(@Path("key") String key, @Path("name") String naam);

    @GET("/auto/{key}/distinct/all")
    Call<HashMap<String, List<AutoInzittendeInfo>>> getAllCars(@Path("key") String key);

    @GET("/auto/{key}/distinct")
    Call<List<AutoEigenaarInfo>> getAllCarOwners(@Path("key") String key);

    @GET("/auto/{key}/removefromcarbyid/{id}")
    Call<DeletedInfo> deleteFromCarById(@Path("key") String key, @Path("id") int id);

    @GET("/auto/{key}/removefromcarbygebruikersnaam/{name}")
    Call<DeletedInfo> deleteFromCarByName(@Path("key") String key, @Path("name") String name);

    @POST("/auto")
    Call<Void> post(@Body AutoPostBody body);

    @POST("/auto/update/taak")
    Call<Void> updateTaak(@Body AutoUpdateTaakPostBody body);

}
