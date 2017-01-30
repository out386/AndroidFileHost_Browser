package browser.afh.types;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import browser.afh.tools.Constants;

public class AfhDevelopers {
    @SerializedName("screenname")
    public final String screenname;
    @SerializedName("flid")
    public final String flid;
    public final String url;

    public AfhDevelopers(String screenname, String flid) {
        this.screenname = screenname;
        this.flid = flid;
        Log.i(Constants.TAG, "AfhDevelopers: " + flid);
        url = Constants.FLID + flid;
    }
}
