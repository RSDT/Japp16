package nl.rsdt.japp.jotial.maps.searching;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-7-2016
 * Description...
 */
public class SearchEntry {

    private String id;

    public String getId() {
        return id;
    }

    private int infoId;

    public int getInfoId() {
        return infoId;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public static class Builder
    {
        SearchEntry entry = new SearchEntry();

        public Builder setId(String string)
        {
            entry.id = string;
            return this;
        }

        public Builder setInfoId(int id)
        {
            entry.infoId = id;
            return this;
        }

        public Builder setValue(String value) {
            entry.value = value;
            return this;
        }

        public SearchEntry create()
        {
            return entry;
        }

    }



}
