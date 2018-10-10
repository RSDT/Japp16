package nl.rsdt.japp.jotial.net.apis;

import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo;
import nl.rsdt.japp.jotial.data.structures.area348.MetaInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
public interface MetaApi {

    @GET("/meta/{key}")
    Call<MetaInfo> getMetaInfo(@Path("key") String key);

    @GET("/meta/{key}/color")
    Call<MetaColorInfo> getMetaColor(@Path("key") String key);
}
