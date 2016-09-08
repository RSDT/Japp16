package nl.rsdt.japp.jotial.net.apis;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.data.structures.area348.FotoOpdrachtInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
public interface FotoApi {

    @GET("/foto/{key}/{id}")
    Call<FotoOpdrachtInfo> getById(@Path("key") String key, @Path("id") int id);

    @GET("/foto/{key}/all")
    Call<ArrayList<FotoOpdrachtInfo>> getAll(@Path("key") String key);

}
