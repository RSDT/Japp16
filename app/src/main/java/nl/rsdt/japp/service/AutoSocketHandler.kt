package nl.rsdt.japp.service

import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import nl.rsdt.japp.jotial.data.nav.Join
import nl.rsdt.japp.jotial.data.nav.Leave
import nl.rsdt.japp.jotial.data.nav.Location
import nl.rsdt.japp.jotial.data.nav.Resend

object AutoSocketHandler {

    var mSocket: Socket? = null
    var newLocationHandlers: MutableList<NavToHandler> = mutableListOf()

    @Synchronized
    private fun setSocket() {
        mSocket?.let { return }
        mSocket = IO.socket("https://auto.jh-rp.nl")
    }

    @Synchronized
    private fun getSocket(): Socket {
        mSocket?.let { return it }
        setSocket()
        establishConnection()
        return mSocket!!
    }

    @Synchronized
    private fun establishConnection() {
        mSocket?.connect()
        mSocket?.on("location") {
            val gson = Gson()
            val jsonString: String = it[0].toString()
            val location: Location = gson.fromJson(jsonString, Location::class.java)
            for (handler in newLocationHandlers){
                handler.onNewLocation(location)
            }
        }
    }

    @Synchronized
    private fun closeConnection() {
        mSocket?.disconnect()
    }
    @Synchronized
    fun join(join: Join){
        val socket = getSocket()
        val gson = Gson()
        val jsonString = gson.toJson(join)
        socket.emit("join", jsonString)
    }
    @Synchronized
    fun location(location: Location){
        val socket = getSocket()
        val gson = Gson()
        val jsonString = gson.toJson(location)
        socket.emit("location", jsonString)
    }
    @Synchronized
    fun resend(resend: Resend){
        val socket = getSocket()
        val gson = Gson()
        val jsonString = gson.toJson(resend)
        socket.emit("resend", jsonString)
    }
    @Synchronized
    fun leave(leave: Leave){
        val socket = getSocket()
        val gson = Gson()
        val jsonString = gson.toJson(leave)
        socket.emit("leave", jsonString)
    }
    @Synchronized
    fun registerNavToHandler(handler: NavToHandler): Boolean {
        return this.newLocationHandlers.add(handler)
    }
    @Synchronized
    fun unregisterNavToHandler(handler: NavToHandler): Boolean {
        return this.newLocationHandlers.remove(handler)
    }
    @Synchronized
    fun init(){
        getSocket()
    }
    fun interface NavToHandler {
        fun onNewLocation(location:Location)
    }
}