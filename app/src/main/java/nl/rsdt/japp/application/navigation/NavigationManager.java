package nl.rsdt.japp.application.navigation;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import com.google.android.material.navigation.NavigationView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.net.API;
import nl.rsdt.japp.jotial.net.DownloadDrawableTask;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
public class NavigationManager extends FragmentNavigationManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "NavigationManager";

    public static final String ACCOUNT_AVATAR_STORAGE = "ACCOUNT_AVATAR";

    private NavigationView navigationView;

    public TextView getUsernameView()  { return ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_name)); }

    public void setUsernameText(String text) { getUsernameView().setText(text); }

    public TextView getRankView() { return ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_rank)); }

    public void setRankText(String text) { getRankView().setText(text); }

    public ImageView getAvatarView() { return ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_avatar));}

    public void setAvatarDrawable(Drawable drawable) { getAvatarView().setImageDrawable(drawable); }

    public NavigationManager() {
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
        JappPreferences.getAppPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void initialize(MainActivity activity) {
        super.initialize(activity);
        navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        setUsernameText(JappPreferences.getAccountUsername());
        setRankText(JappPreferences.getAccountRank());

        if(AppData.hasSave(ACCOUNT_AVATAR_STORAGE)) {
            Drawable drawable = AppData.getDrawable(ACCOUNT_AVATAR_STORAGE);
            if(drawable != null) {
                setAvatarDrawable(drawable);
            }
        } else {
            download();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key)
        {
            case JappPreferences.ACCOUNT_USERNAME:
                getUsernameView().setText(sharedPreferences.getString(key, "Unknown"));
                break;
            case JappPreferences.ACCOUNT_RANK:
                getRankView().setText(sharedPreferences.getString(key, "Unknown"));
                break;
            case JappPreferences.ACCOUNT_AVATAR:
                download();
                break;
        }
    }

    private void download() {
        String filename = JappPreferences.getAccountAvatarName();
        if(!filename.isEmpty()) {
            try {
                DownloadDrawableTask task = new DownloadDrawableTask(new DownloadDrawableTask.OnDowloadDrawablesCompletedCallback() {
                    @Override
                    public void onDownloadDrawablesCompleted(ArrayList<Drawable> drawables) {
                        if(!drawables.isEmpty()) {
                            setAvatarDrawable(drawables.get(0));
                            AppData.saveDrawableInBackground(drawables.get(0), ACCOUNT_AVATAR_STORAGE);
                        }
                    }
                });
                task.execute(new URL(API.SITE_2016_ROOT + "/img/avatar/" + filename));
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
