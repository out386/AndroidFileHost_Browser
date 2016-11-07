package browser.afh;

/**
 * Created by Js on 11/4/2016.
 */

class Device {
    String did;
    String manufacturer;
    String device_name;
    Device( String did, String manufacturer, String device_name) {
        this.did = did;
        this.manufacturer = manufacturer;
        this.device_name = device_name;
    }
}
