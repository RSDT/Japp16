package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.bodies.VosPostBody
import nl.rsdt.japp.jotial.data.structures.area348.VosInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
interface VosApi {

    @GET("/vos/{key}/{team}/last")
    fun getLast(@Path("key") key: String?, @Path("team") team: String): Call<VosInfo>

    @GET("/vos/{key}/{team}/{id}")
    fun getById(@Path("key") key: String?, @Path("team") team: String, @Path("id") id: Int): Call<VosInfo>

    @GET("/vos/{key}/{team}/all")
    fun getAll(@Path("key") key: String?, @Path("team") team: String): Call<ArrayList<VosInfo>>

    @GET("/vos/{key}/{team}/all/{time}")
    fun getAllAfterTime(@Path("key") key: String?, @Path("team") team: String, @Path("time") time: String): Call<ArrayList<VosInfo>>

    @POST("/vos")
    fun post(@Body body: VosPostBody): Call<Void>

}
