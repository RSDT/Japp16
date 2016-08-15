package nl.rsdt.japp.jotial.net;

import com.rsdt.anl.UrlBuilder;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Class that makes Url building a lot easier.
 */
public class ApiUrlBuilder extends UrlBuilder {

    /**
     * The root of the Url.
     * */
    private String root;

    /**
     * The keyword of the url that defines the type of request such as vos.
     * */
    private String keyword;

    /**
     * The value indicating if the API-key should be appended after the keyword.
     * */
    private boolean appendKey = true;

    /**
     * Initializes a new instance of ApiUrlBuilder.
     * */
    public ApiUrlBuilder()
    {
        root = API.API_V2_ROOT;
        url += root;
    }

    /**
     * Initializes a new instance of ApiUrlBuilder.
     * @param appendKey Value indicating if the API-key should be appended after the keyword.
     * */
    public ApiUrlBuilder(boolean appendKey)
    {
        this.appendKey = appendKey;
        root = API.API_V2_ROOT;
        url += root;
    }

    @Override
    /**
     * Appends a String to the Url.
     * */
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

}
