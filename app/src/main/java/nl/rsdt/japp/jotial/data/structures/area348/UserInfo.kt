package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.navigation.NavigationManager
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.net.apis.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-2-2016
 * Class represents that the users info.
 */
class UserInfo
/**
 * Initializes a new instance of UserInfo from the given Parcel.
 *
 * @param in The Parcel where the UserInfo should be created from.
 */
protected constructor(`in`: Parcel) : Parcelable {

    /**
     * The id of the user.
     */
    var id: Int = 0

    /**
     * The username of the user.
     */
    var gebruikersnaam: String? = null

    /**
     * The name of the user.
     */
    var naam: String? = null

    /**
     * The last name of the user.
     */
    var achternaam: String? = null

    /**
     * The email of the user.
     */
    var email: String? = null

    /**
     * The date indicating how long the user has been a member.
     */
    var sinds: String? = null

    /**
     * The date indicating the last time the user was active.
     */
    var last: String? = null

    /**
     * The number indicating if the account of the user is active.
     */
    var actief: Int = 0

    /**
     * The clearance of the user.
     * 0 - Guest
     * 25 - Member
     * 50 - Moderator
     * 75 - Admin
     * As defined in the API docs.
     */
    var toegangslvl: Int = 0

    /**
     * The relative path to the avatar of the user.
     */
    var avatar: String? = null

    /**
     * Converts the users clearance number to text representation of it.
     *
     * @return The text representation of the user's clearance.
     */
    fun rank(): String {
        return intLvlToStringRank(toegangslvl)
    }

    init {
        id = `in`.readInt()
        gebruikersnaam = `in`.readString()
        naam = `in`.readString()
        achternaam = `in`.readString()
        email = `in`.readString()
        sinds = `in`.readString()
        last = `in`.readString()
        actief = `in`.readInt()
        toegangslvl = `in`.readInt()
        avatar = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(gebruikersnaam)
        dest.writeString(naam)
        dest.writeString(achternaam)
        dest.writeString(email)
        dest.writeString(sinds)
        dest.writeString(last)
        dest.writeInt(actief)
        dest.writeInt(toegangslvl)
        dest.writeString(avatar)
    }

    companion object {

        val TAG = "UserInfo"

        fun intActiveToBooleanActive(active: Int): Boolean {
            return active == 1
        }

        /**
         * Converts the users clearance number to text representation of it.
         *
         * @return The text representation of the user's clearance.
         */
        fun intLvlToStringRank(toeganslvl: Int): String {
            /**
             * Allocate String to hold the rank.
             */
            val rank: String

            /**
             * Switch on the toeganslvl, to determine the associated rank name.
             */
            when (toeganslvl) {
                0 -> rank = "Guest"
                25 -> rank = "Member"
                50 -> rank = "Moderator"
                75 -> rank = "Admin"
                else -> rank = "unkown"
            }
            return rank
        }

        val CREATOR: Parcelable.Creator<UserInfo> = object : Parcelable.Creator<UserInfo> {
            override fun createFromParcel(`in`: Parcel): UserInfo {
                return UserInfo(`in`)
            }

            override fun newArray(size: Int): Array<UserInfo?> {
                return arrayOfNulls(size)
            }
        }

        fun collect() {
            val api = Japp.getApi(UserApi::class.java)
            api.getUserInfo(JappPreferences.accountKey).enqueue(object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    if (response.code() == 200) {
                        val info = response.body()

                        if (info != null) {
                            /**
                             * Store the new data in release_preferences.
                             */
                            val userEditor = JappPreferences.visiblePreferences.edit()
                            userEditor.putString(JappPreferences.ACCOUNT_USERNAME, info.gebruikersnaam)
                            userEditor.putString(JappPreferences.ACCOUNT_RANK, info.rank())
                            userEditor.apply()

                            val appEditor = JappPreferences.appPreferences.edit()
                            appEditor.putInt(JappPreferences.ACCOUNT_ID, info.id)
                            appEditor.putString(JappPreferences.ACCOUNT_NAME, info.naam)
                            appEditor.putString(JappPreferences.ACCOUNT_SURNAME, info.achternaam)
                            appEditor.putString(JappPreferences.ACCOUNT_EMAIL, info.email)
                            appEditor.putString(JappPreferences.ACCOUNT_MEMBER_SINCE, info.sinds)
                            appEditor.putString(JappPreferences.ACCOUNT_LAST_VISIT, info.last)
                            appEditor.putBoolean(JappPreferences.ACCOUNT_ACTIVE, intActiveToBooleanActive(info.actief))
                            appEditor.putString(JappPreferences.ACCOUNT_AVATAR, info.avatar)
                            appEditor.apply()

                            Log.i("UserInfo", "New UserInfo was collected")

                            /**
                             * Start a Thread for deleting the InstanceId and deleting avatar.
                             */
                            Thread(Runnable {
                                try {
                                    /**
                                     * Resets Instance ID and revokes all tokens.
                                     */
                                    /**
                                     * Resets Instance ID and revokes all tokens.
                                     */
                                    FirebaseInstanceId.getInstance().deleteInstanceId()

                                    /**
                                     * Delete the Avatar.
                                     */

                                    /**
                                     * Delete the Avatar.
                                     */
                                    AppData.delete(NavigationManager.ACCOUNT_AVATAR_STORAGE)
                                } catch (e: IOException) {
                                    Log.e(TAG, e.toString(), e)
                                }

                                /**
                                 * Get a new token.
                                 */

                                /**
                                 * Get a new token.
                                 */
                                FirebaseInstanceId.getInstance().token
                            }).start()
                        }
                    }
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.e(TAG, t.toString(), t)
                }
            })
        }
    }
}
