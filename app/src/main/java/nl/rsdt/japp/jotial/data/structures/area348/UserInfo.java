package nl.rsdt.japp.jotial.data.structures.area348;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-2-2016
 * Class represents that the users info.
 */
public class UserInfo implements Parcelable {

    /**
     * The id of the user.
     * */
    public int id;

    /**
     * The username of the user.
     * */
    public String gebruikersnaam;

    /**
     * The name of the user.
     * */
    public String naam;

    /**
     * The last name of the user.
     * */
    public String achternaam;

    /**
     * The email of the user.
     * */
    public String email;

    /**
     * The date indicating how long the user has been a member.
     * */
    public String sinds;

    /**
     * The date indicating the last time the user was active.
     * */
    public String last;

    /**
     * The number indicating if the account of the user is active.
     * */
    public int actief;

    /**
     * The clearance of the user.
     * 0 - Guest
     * 25 - Member
     * 50 - Moderator
     * 75 - Admin
     * As defined in the API docs.
     * */
    public int toeganslvl;

    /**
     * The relative path to the avatar of the user.
     * */
    public String avatar;

    /**
     * Converts the users clearance number to text representation of it.
     *
     * @return The text representation of the user's clearance.
     * */
    public String rank() { return intLvlToStringRank(toeganslvl); }

    /**
     * Initializes a new instance of UserInfo from the given Parcel.
     *
     * @param in The Parcel where the UserInfo should be created from.
     * */
    protected UserInfo(Parcel in) {
        id = in.readInt();
        gebruikersnaam = in.readString();
        naam = in.readString();
        achternaam = in.readString();
        email = in.readString();
        sinds = in.readString();
        last = in.readString();
        actief = in.readInt();
        toeganslvl = in.readInt();
        avatar = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(gebruikersnaam);
        dest.writeString(naam);
        dest.writeString(achternaam);
        dest.writeString(email);
        dest.writeString(sinds);
        dest.writeString(last);
        dest.writeInt(actief);
        dest.writeInt(toeganslvl);
        dest.writeString(avatar);
    }

    public static boolean intActiveToBooleanActive(int active) {
        return (active == 1);
    }

    /**
     * Converts the users clearance number to text representation of it.
     *
     * @return The text representation of the user's clearance.
     * */
    public static String intLvlToStringRank(int toeganslvl)
    {
        /**
         * Allocate String to hold the rank.
         * */
        String rank;

        /**
         * Switch on the toeganslvl, to determine the associated rank name.
         * */
        switch (toeganslvl)
        {
            case 0:
                rank = "Guest";
                break;
            case 25:
                rank = "Member";
                break;
            case 50:
                rank = "Moderator";
                break;
            case 75:
                rank = "Admin";
                break;
            default:
                rank = "unkown";
                break;
        }
        return rank;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public static void collect()
    {
        WebRequest request = new WebRequest.Builder()
                .setUrl(new ApiUrlBuilder(true).append("gebruiker").append("info").build())
                .setMethod(WebRequestMethod.GET)
                .create();

        request.executeAsync(new WebRequest.OnWebRequestCompletedCallback() {
            @Override
            public void onWebRequestCompleted(WebResponse response) {
                if(response.getResponseCode() == 200)
                {
                    UserInfo info = new Gson().fromJson(response.getData(), UserInfo.class);

                    if(info != null)
                    {
                        /**
                         * Store the new data in preferences.
                         * */
                        SharedPreferences.Editor userEditor = JappPreferences.getUserPreferences().edit();
                        userEditor.putString(JappPreferences.ACCOUNT_USERNAME, info.gebruikersnaam);
                        userEditor.putString(JappPreferences.ACCOUNT_RANK, info.rank());
                        userEditor.apply();

                        SharedPreferences.Editor appEditor = JappPreferences.getAppPreferences().edit();
                        appEditor.putInt(JappPreferences.ACCOUNT_ID, info.id);
                        appEditor.putString(JappPreferences.ACCOUNT_NAME, info.naam);
                        appEditor.putString(JappPreferences.ACCOUNT_SURNAME, info.achternaam);
                        appEditor.putString(JappPreferences.ACCOUNT_EMAIL, info.email);
                        appEditor.putString(JappPreferences.ACCOUNT_MEMBER_SINCE, info.sinds);
                        appEditor.putString(JappPreferences.ACCOUNT_LAST_VISIT, info.last);
                        appEditor.putBoolean(JappPreferences.ACCOUNT_ACTIVE, intActiveToBooleanActive(info.actief));
                        appEditor.putString(JappPreferences.ACCOUNT_RANK, intLvlToStringRank(info.toeganslvl));
                        appEditor.putString(JappPreferences.ACCOUNT_AVATAR, info.avatar);
                        appEditor.apply();

                        Log.i("UserInfo", "New UserInfo was collected");
                    }
                }
            }
        });
    }

}
