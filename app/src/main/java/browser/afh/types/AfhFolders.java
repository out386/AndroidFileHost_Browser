package browser.afh.types;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhFolders {
	@SerializedName("MESSAGE")
    private final String message;
    @SerializedName("DATA")
    public final FolderContent data;

	public AfhFolders(String message, FolderContent data) {
		this.message = message;
		this.data = data;
    }

    public class FolderContent {
        @SerializedName("files")
        public final List<Files> files;
        @SerializedName("folders")
        public final List<AfhDevelopers.Developer> folders;

        public FolderContent(List<Files> files, List<AfhDevelopers.Developer> folders) {
            this.files = files;
            this.folders = folders;
        }
    }

    public class Files
    {
        @SerializedName("name")
        public String name;
        @SerializedName("url")
        public String url;
        @SerializedName("file_size")
        public String file_size;
        @SerializedName("upload_date")
        public String upload_date;
        @SerializedName("downloads")
        public int downloads;
        public String screenname;
    }
}