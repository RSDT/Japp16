package nl.rsdt.japp.application.showcase;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.lang.reflect.Field;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.activities.MainActivity;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 9-9-2016
 * Description...
 */
public class JappShowcaseSequence extends ShowcaseSequence<MainActivity> {

    public JappShowcaseSequence(MainActivity mainActivity) {
        setActivity(mainActivity);
        populate();
    }

    protected void populate() {
        members.add(new ShowcaseSequenceItem() {
            @Override
            public String getTitle() {
                return "Navigatie Menu";
            }

            @Override
            public String getContentText() {
                return "Vanuit hier kun je navigeren naar verschillende pagina's";
            }

            @Override
            public ViewTarget getTarget() {
                try {
                    Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
                    Field field = Toolbar.class.getDeclaredField("mNavButtonView");
                    field.setAccessible(true);
                    View navigationView = (View) field.get(toolbar);
                    return new ViewTarget(navigationView);

                } catch (Exception e) {
                    Log.e("ShowcaseSequence", e.toString(), e);
                }
                return null;
            }
        });

        members.add(new ShowcaseSequenceItem() {
            @Override
            public String getTitle() {
                return "Refresh Knop";
            }

            @Override
            public String getContentText() {
                return "Hiermee kun je de app handmatig laten updaten, al is dit vaak niet nodig omdat de app zichzelf update";
            }

            @Override
            public ViewTarget getTarget() {
                return new ViewTarget(R.id.refresh, activity);
            }
        });

        members.add(new ShowcaseSequenceItem() {

            @Override
            public String getTitle() {
                return "Actie Menu";
            }

            @Override
            public String getContentText() {
                return "Vanuit hier kun je acties ondernemen afhankelijk van de pagina waarop je bent";
            }

            @Override
            public ViewTarget getTarget() {
                FloatingActionMenu menu = (FloatingActionMenu)activity.findViewById(R.id.fab_menu);
                menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                    @Override
                    public void onMenuToggle(boolean opened) {
                        if(opened) {
                            current.hide();
                        }
                    }
                });
                return new ViewTarget(menu.getMenuIconView());
            }

            @Override
            public OnShowcaseEventListener getEventListener() {
                return new SimpleShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        FloatingActionMenu menu = ((FloatingActionMenu) activity.findViewById(R.id.fab_menu));
                        menu.open(true);
                        continueToNext();
                        menu.setOnMenuToggleListener(null);
                    }
                };
            }

        });

        members.add(new ShowcaseSequenceItem() {
            @Override
            public String getTitle() {
                return "Volg Mij Knop";
            }

            @Override
            public String getContentText() {
                return "Met deze knop kan je jezelf laten volgen op de kaart, je kunt zelf bepalen hoe ver en in welke hoek de camera moet staan tijdens het volgen";
            }

            @Override
            public ViewTarget getTarget() {
                FloatingActionButton follow = (FloatingActionButton)activity.findViewById(R.id.fab_follow);
                return new ViewTarget(follow);
            }
        });

        members.add(new ShowcaseSequenceItem() {
            @Override
            public String getTitle() {
                return "Mark Knop";
            }

            @Override
            public String getContentText() {
                return "Met deze knop kan je voor jezelf iets markeren op de kaart";
            }

            @Override
            public ViewTarget getTarget() {
                FloatingActionButton mark = (FloatingActionButton)activity.findViewById(R.id.fab_mark);
                return new ViewTarget(mark);
            }
        });

        members.add(new ShowcaseSequenceItem() {
            @Override
            public String getTitle() {
                return "Spot Knop";
            }

            @Override
            public String getContentText() {
                return "Met deze knop kun je een vos spotten, je selecteert een locatie op de kaart en voegd eventueel wat informatie toe";
            }

            @Override
            public ViewTarget getTarget() {
                FloatingActionButton spot = (FloatingActionButton)activity.findViewById(R.id.fab_spot);
                return new ViewTarget(spot);
            }
        });

        members.add(new ShowcaseSequenceItem() {
            @Override
            public String getTitle() {
                return "Hunt Knop";
            }

            @Override
            public String getContentText() {
                return "Met deze knop kun je een vos hunten, je selecteert een locatie op de kaart en voegd eventueel wat informatie toe";
            }

            @Override
            public ViewTarget getTarget() {
                FloatingActionButton hunt = (FloatingActionButton)activity.findViewById(R.id.fab_hunt);
                return new ViewTarget(hunt);
            }
        });


    }
}
