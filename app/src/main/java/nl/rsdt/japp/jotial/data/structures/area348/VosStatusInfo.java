package nl.rsdt.japp.jotial.data.structures.area348;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-10-2016
 * Description...
 */

public class VosStatusInfo {

    @SerializedName("data")
    protected ArrayList<Status> status = new ArrayList<>();

    public ArrayList<Status> getStatus() {
        return status;
    }

    public static class Status {

        @SerializedName("team")
        protected String team;

        public String getTeam() {
            return team;
        }

        @SerializedName("status")
        protected State status;

        public State getStatus() {
            return status;
        }
    }

    public enum State {
        @SerializedName("rood")
        RED,

        @SerializedName("oranje")
        ORANGE,

        @SerializedName("groen")
        GREEN
    }


}
