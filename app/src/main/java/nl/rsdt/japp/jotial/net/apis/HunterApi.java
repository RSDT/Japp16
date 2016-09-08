package nl.rsdt.japp.jotial.net.apis;

import java.util.ArrayList;
import java.util.HashMap;

import nl.rsdt.japp.jotial.data.bodies.HunterPostBody;
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo;
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
public interface HunterApi {

    @GET("/hunter/{key}/all")
    Call<HashMap<String, ArrayList<HunterInfo>>> getAll(@Path("key") String key);

    @GET("/hunter/{key}/all/{time}")
    Call<HashMap<String, ArrayList<HunterInfo>>> getAllAfterTime(@Path("key") String key, @Path("time") String time);

    @GET("/hunter/{key}/all/last")
    Call<HashMap<String, ArrayList<HunterInfo>>> getAllLast(@Path("key") String key);

    @GET("/hunter/{key}/andere/{user}")
    Call<HashMap<String, ArrayList<HunterInfo>>> getAllExcept(@Path("key") String key, @Path("user") String user);

    @POST("/hunter")
    Call<Void> post(@Body HunterPostBody body);

}
