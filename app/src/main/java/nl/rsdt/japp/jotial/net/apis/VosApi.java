package nl.rsdt.japp.jotial.net.apis;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.data.bodies.VosPostBody;
import nl.rsdt.japp.jotial.data.structures.area348.VosInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
public interface VosApi {

    @GET("/vos/{key}/{team}/last")
    Call<VosInfo> getLast(@Path("key") String key, @Path("team") String team);

    @GET("/vos/{key}/{team}/{id}")
    Call<VosInfo> getById(@Path("key") String key, @Path("team") String team, @Path("id") int id);

    @GET("/vos/{key}/{team}/all")
    Call<ArrayList<VosInfo>> getAll(@Path("key") String key, @Path("team") String team);

    @GET("/vos/{key}/{team}/all/{time}")
    Call<ArrayList<VosInfo>> getAllAfterTime(@Path("key") String key, @Path("team") String team, @Path("time") String time);

    @POST("/vos")
    Call<Void> post(@Body VosPostBody body);

}
