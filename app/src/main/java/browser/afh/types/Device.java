package browser.afh.types;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Device {
    @SerializedName("MESSAGE")
    public final String message;
    @SerializedName("DATA")
    public final List<DeviceData> data;
    public Device(String message, List<DeviceData> data) {
        this.message = message;
        this.data = data;
    }
}
