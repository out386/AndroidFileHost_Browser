package browser.afh.types;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhFolderContentData {
    @SerializedName("files")
    public final List<AfhFiles> files;
	@SerializedName("folders")
    public final List<AfhFolders> folders;

	public AfhFolderContentData(List<AfhFiles> files, List<AfhFolders> folders) {
		this.files = files;
		this.folders = folders;
    }
}