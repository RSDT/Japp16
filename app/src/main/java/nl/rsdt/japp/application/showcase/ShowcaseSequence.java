package nl.rsdt.japp.application.showcase;

import android.app.Activity;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;


import java.util.ArrayList;

import nl.rsdt.japp.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class ShowcaseSequence<A extends Activity> {

    protected int count = 0;

    protected ShowcaseView current;

    protected ArrayList<ShowcaseSequenceItem> members = new ArrayList<>();

    protected A activity;

    protected OnSequenceCompletedCallback<A> callback;

    public void setActivity(A activity) {
        this.activity = activity;
    }

    public void setCallback(OnSequenceCompletedCallback<A> callback) {
        this.callback = callback;
    }

    public void start() {
        if(members != null) {
            if(members.size() > count) {
                ShowcaseSequenceItem member = members.get(count);
                ShowcaseView.Builder builder = new ShowcaseView.Builder(activity);
                current = builder.setTarget(member.getTarget())
                        .setStyle(R.style.ShowCaseTheme)
                        .withMaterialShowcase()
                        .setContentTitle(member.getTitle())
                        .setContentText(member.getContentText())
                        .setShowcaseEventListener(member.getEventListener())
                        .build();
                current.show();
            } else {
                if(callback != null) {
                    callback.onSequenceCompleted(this);
                }
            }
        }
    }

    public void end() {
        onDestroy();
    }

    private void onDestroy() {
        if(current != null) {
            current.setOnShowcaseEventListener(null);
            current.hide();
            current = null;
        }

        if(members != null) {
            members.clear();
            members = null;
        }

        if(activity != null) {
            activity = null;
        }

        if(callback != null) {
            callback = null;
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 29-8-2016
     * Description...
     */
    public abstract class ShowcaseSequenceItem {

        public abstract String getTitle();

        public abstract String getContentText();

        public abstract ViewTarget getTarget();

        public OnShowcaseEventListener getEventListener() { return new SimpleShowcaseEventListener() {
            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                count++;
                start();
            }
        }; }

    }


    public interface OnSequenceCompletedCallback<A extends Activity> {
        void onSequenceCompleted(ShowcaseSequence<A> sequence);
    }

}
