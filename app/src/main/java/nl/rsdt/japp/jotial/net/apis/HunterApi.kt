package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.bodies.HunterPostBody
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo
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
interface HunterApi {

    @GET("/hunter/{key}/all")
    fun getAll(@Path("key") key: String?): Call<HashMap<String, ArrayList<HunterInfo>>>

    @GET("/hunter/{key}/all/{time}")
    fun getAllAfterTime(@Path("key") key: String?, @Path("time") time: String): Call<HashMap<String, ArrayList<HunterInfo>>>

    @GET("/hunter/{key}/all/last")
    fun getAllLast(@Path("key") key: String?): Call<HashMap<String, ArrayList<HunterInfo>>>

    @GET("/hunter/{key}/andere/{user}")
    fun getAllExcept(@Path("key") key: String?, @Path("user") user: String): Call<HashMap<String, ArrayList<HunterInfo>>>

    @POST("/hunter")
    fun post(@Body body: HunterPostBody): Call<Void>

}
