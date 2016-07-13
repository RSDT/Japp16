package nl.rsdt.japp.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import nl.rsdt.japp.jotial.io.AppData;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class JappPreferences {

    public static final String FIRST_RUN = "pref_first_run";

    public static final String ACCOUNT_ID = "pref_account_id";

    public static final String ACCOUNT_USERNAME = "pref_account_username";

    public static final String ACCOUNT_NAME = "pref_account_name";

    public static final String ACCOUNT_SURNAME = "pref_account_surname";

    public static final String ACCOUNT_EMAIL = "pref_account_email";

    public static final String ACCOUNT_MEMBER_SINCE = "pref_account_since";

    public static final String ACCOUNT_LAST_VISIT = "pref_account_last";

    public static final String ACCOUNT_ACTIVE = "pref_account_active";

    public static final String ACCOUNT_AVATAR = "pref_account_avatar";

    public static final String ACCOUNT_RANK = "pref_account_rank";

    public static final String ACCOUNT_ICON = "pref_icon_icon";

    public static final String ACCOUNT_KEY = "pref_account_key";


    public static SharedPreferences getUserPreferences() {
        return Japp.getPreferences();
    }

    public static SharedPreferences getAppPreferences() {
        return Japp.getInstance().getSharedPreferences("nl.rsdt.japp", Context.MODE_PRIVATE);
    }

    public static boolean isFirstRun() {
        return getAppPreferences().getBoolean(FIRST_RUN, true);
    }

    public static String getAccountUsername() {
        return getUserPreferences().getString(ACCOUNT_USERNAME, "unknown");
    }

    public static String getAccountRank() {
        return getUserPreferences().getString(ACCOUNT_RANK, "Guest");
    }

    public static int getAccountIcon() {
        return Integer.valueOf(getUserPreferences().getString(ACCOUNT_ICON, "0"));
    }

    public static String getAccountKey() {
        return getUserPreferences().getString(ACCOUNT_KEY, "");
    }

}
