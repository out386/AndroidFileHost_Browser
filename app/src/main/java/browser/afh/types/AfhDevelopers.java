package browser.afh.types;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhDevelopers {
	@SerializedName("MESSAGE")
    private final String message;
    @SerializedName("DATA")
    public final List<Developer> data;
	
	public AfhDevelopers(String message, List<Developer> data) {
		this.message = message;
		this.data = data;
    }

    public class Developer {
        @SerializedName("screenname")
        public String screenname;
        @SerializedName("flid")
        public String flid;
        @SerializedName("name")
        public String name;

    }
}