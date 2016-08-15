package nl.rsdt.japp.jotial.net;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
public class DownloadDrawableTask extends AsyncTask<URL, Integer, ArrayList<Drawable>> {

    private OnDowloadDrawablesCompletedCallback callback;

    public DownloadDrawableTask(OnDowloadDrawablesCompletedCallback callback) {
        this.callback = callback;
    }

    @Override
    protected ArrayList<Drawable> doInBackground(URL... params) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        URL url;
        for(int i = 0; i < params.length; i++) {
            url = params[i];
            if(url != null) {
                try {
                    InputStream is = (InputStream)url.getContent();
                    drawables.add(Drawable.createFromStream(is, url.getFile()));
                }
                catch (Exception e) {
                    Log.e("UserInfo", e.toString(), e);
                }
            }
        }
        return drawables;
    }

    @Override
    protected void onPostExecute(ArrayList<Drawable> drawables) {
        super.onPostExecute(drawables);

        if(callback != null) {
            callback.onDownloadDrawablesCompleted(drawables);
        }
    }

    public interface OnDowloadDrawablesCompletedCallback {
        void onDownloadDrawablesCompleted(ArrayList<Drawable> drawables);
    }
}
