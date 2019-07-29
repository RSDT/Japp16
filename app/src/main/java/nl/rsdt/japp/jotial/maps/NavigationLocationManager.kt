package nl.rsdt.japp.jotial.maps

import com.google.firebase.database.*
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.firebase.Location
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.net.apis.AutoApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by mattijn on 30/09/17.
 */

class NavigationLocationManager : ChildEventListener, ValueEventListener {
    private var callback: OnNewLocation? = null
    private var oldLoc: Location? = null

    init {
        val reference = FirebaseDatabase.getInstance().reference.child(FDB_NAME)
        reference.addValueEventListener(this)
    }

    private fun update(snapshot: DataSnapshot, child: String?) {
        val id = JappPreferences.accountId
        if (id >= 0) {
            val api = Japp.getApi(AutoApi::class.java)
            api.getInfoById(JappPreferences.accountKey, id).enqueue(object : Callback<AutoInzittendeInfo> {
                override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                    if (response.code() == 200) {
                        val info = response.body()
                        val loc = snapshot.child(info!!.autoEigenaar!!).getValue(Location::class.java)
                        if (oldLoc != null && oldLoc != loc || oldLoc == null && loc != null) {
                            oldLoc = loc
                            if (callback != null) {
                                callback!!.onNewLocation(loc)
                            }
                        }
                    } else if (response.code() == 404) {
                        if (callback != null) {
                            callback!!.onNotInCar()
                        }
                    }
                }

                override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {
                    throw RuntimeException(t)
                }
            })
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
        update(dataSnapshot, s)
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
        update(dataSnapshot, s)
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {

    }

    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        update(dataSnapshot, dataSnapshot.key)
    }

    override fun onCancelled(databaseError: DatabaseError) {
        throw databaseError.toException()
    }

    fun setCallback(callback: OnNewLocation) {
        this.callback = callback
    }

    interface OnNewLocation {
        fun onNewLocation(location: Location?)
        fun onNotInCar()
    }

    companion object {
        val FDB_NAME = "autos"
    }
}
