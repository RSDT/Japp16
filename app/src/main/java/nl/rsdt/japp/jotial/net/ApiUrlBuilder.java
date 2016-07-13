package nl.rsdt.japp.jotial.net;

import com.rsdt.anl.UrlBuilder;

import java.net.URL;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
public class ApiUrlBuilder extends UrlBuilder {


    private String root;

    private String keyword;

    private boolean appendKey = true;

    public ApiUrlBuilder()
    {
        root = API.API_V2;
        url += root;
    }

    public ApiUrlBuilder(boolean appendKey)
    {
        this.appendKey = appendKey;
        root = API.API_V2;
        url += root;
    }

    @Override
    public UrlBuilder append(String arg) {
        if(keyword == null)
        {
            keyword = arg;

            super.append("/" + arg);

            if(appendKey)
            {
                super.append("/" + JappPreferences.getAccountKey());
            }
            return this;
        }
        return super.append("/" + arg);
    }

    @Override
    public URL build() {
        return super.build();
    }
}
