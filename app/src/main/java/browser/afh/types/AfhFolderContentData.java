package browser.afh.types;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhFolderContentData {
    @SerializedName("files")
    public final List<AfhFiles> files;
	@SerializedName("folders")
    public final List<AfhDevelopers> folders;

	public AfhFolderContentData(List<AfhFiles> files, List<AfhDevelopers> folders) {
		this.files = files;
		this.folders = folders;
    }
}