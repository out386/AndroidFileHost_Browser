package browser.afh.types;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhDevices {
    @SerializedName("MESSAGE")
    public final String message;
    @SerializedName("DATA")
    public final List<Device> data;
    public AfhDevices(String message, List<Device> data) {
        this.message = message;
        this.data = data;
    }

    public class Device {
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
