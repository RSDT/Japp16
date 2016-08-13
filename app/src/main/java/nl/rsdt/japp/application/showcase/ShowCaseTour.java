package nl.rsdt.japp.application.showcase;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionMenu;


import java.lang.reflect.Field;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.activities.MainActivity;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class ShowCaseTour {

    private int count = 0;

    private MainActivity mainActivity;

    public ShowCaseTour(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public void showcase() {

        ShowcaseView.Builder builder = new ShowcaseView.Builder(mainActivity);

        ViewTarget target = null;
        String title = "";
        String text = "";
        switch (count)
        {
            case 0:
                try {
                    Toolbar toolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar);
                    Field field = Toolbar.class.getDeclaredField("mNavButtonView");
                    field.setAccessible(true);
                    View navigationView = (View) field.get(toolbar);
                    target = new ViewTarget(navigationView);

                    title = "Navigatie Menu";
                    text = "Vanuit hier kun je navigeren naar verschillende pagina's";

                } catch (Exception e) {
                    Log.e("ShowCaseTour", e.toString(), e);
                }
                break;
            case 1:
                FloatingActionMenu menu = (FloatingActionMenu)mainActivity.findViewById(R.id.fab_menu);
                target = new ViewTarget(menu.getMenuIconView());

                title = "Actie Menu";
                text = "Vanuit hier kun je acties ondernemen afhankelijk van de pagina waarop je bent";
                break;
            case 2:
                return;
        }

        builder.setTarget(target)
                .setStyle(R.style.ShowCaseTheme)
                .setContentTitle(title)
                .setContentText(text)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        count++;
                        showcase();
                    }
                })
                .build()
                .show();
    }

}
