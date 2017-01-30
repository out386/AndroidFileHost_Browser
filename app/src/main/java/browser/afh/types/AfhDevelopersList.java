package browser.afh.types;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhDevelopersList {
	@SerializedName("MESSAGE")
    public final String message;
    @SerializedName("DATA")
    public final List<AfhDevelopers> data;
	
	public AfhDevelopersList(String message, List<AfhDevelopers> data) {
		this.message = message;
		this.data = data;
    }
}