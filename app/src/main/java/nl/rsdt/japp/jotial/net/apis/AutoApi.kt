package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.bodies.AutoPostBody
import nl.rsdt.japp.jotial.data.bodies.AutoUpdateTaakPostBody
import nl.rsdt.japp.jotial.data.structures.area348.AutoEigenaarInfo
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.data.structures.area348.DeletedInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*

/**
 * Created by mattijn on 30/09/17.
 */

interface AutoApi {

    @GET("/auto/{key}/info/{id}")
    fun getInfoById(@Path("key") key: String, @Path("id") id: Int): Call<AutoInzittendeInfo>

    @GET("/auto/{key}/onecar/{id}")
    fun getCarById(@Path("key") key: String, @Path("id") id: Int): Call<ArrayList<AutoInzittendeInfo>>

    @GET("/auto/{key}/onecar/{name}")
    fun getCarByName(@Path("key") key: String, @Path("name") naam: String): Call<ArrayList<AutoInzittendeInfo>>

    @GET("/auto/{key}/distinct/all")
    fun getAllCars(@Path("key") key: String): Call<HashMap<String, List<AutoInzittendeInfo>>>

    @GET("/auto/{key}/distinct")
    fun getAllCarOwners(@Path("key") key: String): Call<List<AutoEigenaarInfo>>

    @GET("/auto/{key}/removefromcarbyid/{id}")
    fun deleteFromCarById(@Path("key") key: String, @Path("id") id: Int): Call<DeletedInfo>

    @GET("/auto/{key}/removefromcarbygebruikersnaam/{name}")
    fun deleteFromCarByName(@Path("key") key: String, @Path("name") name: String): Call<DeletedInfo>

    @POST("/auto")
    fun post(@Body body: AutoPostBody): Call<Void>

    @POST("/auto/update/taak")
    fun updateTaak(@Body body: AutoUpdateTaakPostBody): Call<Void>

}
