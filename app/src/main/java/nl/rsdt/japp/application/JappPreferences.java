package nl.rsdt.japp.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for preferences
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

    public static final String UPDATES_AUTO = "pref_updates_auto";

    public static final String MAP_TYPE = "pref_map_type";

    /**
     * Gets the visible preferences of Japp.
     * */
    public static SharedPreferences getVisiblePreferences() {
        return PreferenceManager.getDefaultSharedPreferences(Japp.getInstance());
    }

    /**
     * Gets the non-visible preferences of Japp.
     * */
    public static SharedPreferences getAppPreferences() {
        return Japp.getInstance().getSharedPreferences("nl.rsdt.japp", Context.MODE_PRIVATE);
    }

    /**
     * Gets the value indicating if this is the first run.
     * */
    public static boolean isFirstRun() {
        return getAppPreferences().getBoolean(FIRST_RUN, true);
    }

    /**
     * Sets the value determining if this is the first run.
     * */
    public static void setFirstRun(boolean value) { getAppPreferences().edit().putBoolean(FIRST_RUN, value).apply(); }

    /**
     * Gets the username of the active account.
     * */
    public static String getAccountUsername() {
        return getVisiblePreferences().getString(ACCOUNT_USERNAME, "unknown");
    }

    /**
     * Gets the rank of the active account.
     * */
    public static String getAccountRank() {
        return getVisiblePreferences().getString(ACCOUNT_RANK, "Guest");
    }

    /**
     * Gets the icon that the user has selected to be displayed to the others.
     * */
    public static int getAccountIcon() {
        return Integer.valueOf(getVisiblePreferences().getString(ACCOUNT_ICON, "0"));
    }

    /**
     * Gets the API-key associated with the active account.
     * */
    public static String getAccountKey() {
        return getVisiblePreferences().getString(ACCOUNT_KEY, "");
    }

    /**
     * Gets the filename of the avatar on the area348 server of the active account.
     * */
    public static String getAccountAvatarName() {
        return getAppPreferences().getString(ACCOUNT_AVATAR, "");
    }

    /**
     * Gets the value indicating if auto update is enabled.
     * */
    public static boolean isAutoUpdateEnabled() {
        return getVisiblePreferences().getBoolean(UPDATES_AUTO, true);
    }

    /**
     * Sets the value indicating if auto update is enabled.
     * */
    public static void setAutoUpdateEnabled(boolean value) {
        getVisiblePreferences().edit().putBoolean(UPDATES_AUTO, value).apply();
    }


}
