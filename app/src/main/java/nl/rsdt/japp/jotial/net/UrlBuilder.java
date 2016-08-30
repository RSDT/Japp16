package nl.rsdt.japp.jotial.net;

import java.net.URL;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 22-6-2016
 * Description...
 */
public class UrlBuilder {

    protected String url = "";

    public UrlBuilder append(String arg)
    {
        url += arg;
        return this;
    }

    public URL buildAsUrl()
    {
        try
        {
            return new URL(url);
        } catch (Exception e)
        {
            return null;
        }
    }

    public String buildAsString() {
        return url;
    }

}
