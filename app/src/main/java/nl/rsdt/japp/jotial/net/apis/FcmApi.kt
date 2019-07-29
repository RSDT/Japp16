package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.bodies.FcmPostBody
import nl.rsdt.japp.jotial.data.structures.area348.FcmUserTokenValidationInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 7-9-2016
 * Description...
 */
interface FcmApi {

    @POST("/fcm")
    fun postToken(@Body body: FcmPostBody): Call<Void>

    @GET("/fcm/{key}/{user}/{token}")
    fun validateUserToken(@Path("key") key: String?, @Path("user") user: String, @Path("token") token: String): Call<FcmUserTokenValidationInfo>

}
