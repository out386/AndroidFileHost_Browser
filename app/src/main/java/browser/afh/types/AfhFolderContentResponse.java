package browser.afh.types;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhFolderContentResponse {
	@SerializedName("MESSAGE")
    public final String message;
    @SerializedName("DATA")
    public final AfhFolderContentData data;

	public AfhFolderContentResponse(String message, AfhFolderContentData data) {
		this.message = message;
		this.data = data;
    }
}