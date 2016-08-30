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
import java.util.ArrayList;

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

    private ShowcaseView current;

    private ArrayList<ShowCaseTourMember> members = new ArrayList<>();

    private MainActivity mainActivity;

    public ShowCaseTour(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        populate();
    }

    private void populate() {
        members.add(new ShowCaseTourMember() {
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
                    Toolbar toolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar);
                    Field field = Toolbar.class.getDeclaredField("mNavButtonView");
                    field.setAccessible(true);
                    View navigationView = (View) field.get(toolbar);
                    return new ViewTarget(navigationView);

                } catch (Exception e) {
                    Log.e("ShowCaseTour", e.toString(), e);
                }
                return null;
            }
        });

        members.add(new ShowCaseTourMember() {
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
                FloatingActionMenu menu = (FloatingActionMenu)mainActivity.findViewById(R.id.fab_menu);
                menu.setOnMenuButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        current.hide();
                        ((FloatingActionMenu) mainActivity.findViewById(R.id.fab_menu)).open(true);
                    }
                });
                return new ViewTarget(menu.getMenuIconView());
            }

            @Override
            public OnShowcaseEventListener getEventListener() {
                return new SimpleShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        FloatingActionMenu menu = ((FloatingActionMenu) mainActivity.findViewById(R.id.fab_menu));
                        menu.setOnMenuButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FloatingActionMenu menu = ((FloatingActionMenu) mainActivity.findViewById(R.id.fab_menu));
                                if(menu.isOpened()) {
                                    menu.close(true);
                                } else {
                                    menu.open(true);
                                }
                            }
                        });
                        menu.open(true);
                        count++;
                        showcase();
                    }
                };
            }
        });

        members.add(new ShowCaseTourMember() {
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
                FloatingActionButton follow = (FloatingActionButton)mainActivity.findViewById(R.id.fab_follow);
                return new ViewTarget(follow);
            }
        });

        members.add(new ShowCaseTourMember() {
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
                FloatingActionButton mark = (FloatingActionButton)mainActivity.findViewById(R.id.fab_mark);
                return new ViewTarget(mark);
            }
        });

        members.add(new ShowCaseTourMember() {
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
                FloatingActionButton spot = (FloatingActionButton)mainActivity.findViewById(R.id.fab_spot);
                return new ViewTarget(spot);
            }
        });

        members.add(new ShowCaseTourMember() {
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
                FloatingActionButton hunt = (FloatingActionButton)mainActivity.findViewById(R.id.fab_hunt);
                return new ViewTarget(hunt);
            }
        });


    }

    public void showcase() {
        if(members.size() > count) {
            ShowCaseTourMember member = members.get(count);
            ShowcaseView.Builder builder = new ShowcaseView.Builder(mainActivity);
            current = builder.setTarget(member.getTarget())
                    .setStyle(R.style.ShowCaseTheme)
                    .setContentTitle(member.getTitle())
                    .setContentText(member.getContentText())
                    .setShowcaseEventListener(member.getEventListener())
                    .build();
            current.show();
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 29-8-2016
     * Description...
     */
    public abstract class ShowCaseTourMember {

        public abstract String getTitle();

        public abstract String getContentText();

        public abstract ViewTarget getTarget();

        public OnShowcaseEventListener getEventListener() { return new SimpleShowcaseEventListener() {
            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                count++;
                showcase();
            }
        }; }
    }

}
