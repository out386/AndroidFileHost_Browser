package browser.afh.types;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhDevice {
    @SerializedName("MESSAGE")
    public final String message;
    @SerializedName("DATA")
    public final List<Data> data;
    public AfhDevice(String message, List<Data> data) {
        this.message = message;
        this.data = data;
    }

    public class Data {
        @SerializedName("did")
        public String did;
        @SerializedName("manufacturer")
        public String manufacturer;
        @SerializedName("device_name")
        public String device_name;
        @SerializedName("image")
        public String image;
    }
}
