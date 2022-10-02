package nl.rsdt.japp.service

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object AutoSocketHandler {

    var mSocket: Socket? = null

    @Synchronized
    fun setSocket() {
        mSocket?.let { return }
        mSocket = IO.socket("https://auto.jh-rp.nl")
    }

    @Synchronized
    fun getSocket(): Socket {
        mSocket?.let { return it }
        setSocket()
        establishConnection()
        return mSocket!!
    }

    @Synchronized
    fun establishConnection() {
        mSocket?.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket?.disconnect()
    }
}