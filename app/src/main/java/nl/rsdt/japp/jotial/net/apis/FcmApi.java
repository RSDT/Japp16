package nl.rsdt.japp.jotial.net.apis;

import nl.rsdt.japp.jotial.data.bodies.FcmPostBody;
import nl.rsdt.japp.jotial.data.structures.area348.FcmUserTokenValidationInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 7-9-2016
 * Description...
 */
public interface FcmApi {

    @POST("/fcm")
    Call<Void> postToken(@Body FcmPostBody body);

    @GET("/fcm/{key}/{user}/{token}")
    Call<FcmUserTokenValidationInfo> validateUserToken(@Path("key") String key, @Path("user") String user, @Path("token") String token);

}
