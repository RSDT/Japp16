package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.auth.Authentication
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
interface AuthApi {

    @POST("/login")
    fun login(@Body body: Authentication.LoginBody): Call<Authentication.KeyObject>

    @GET("/gebruiker/{key}/sleutelExists")
    fun validateKey(@Path("key") key: String): Call<Authentication.ValidateObject>

}
