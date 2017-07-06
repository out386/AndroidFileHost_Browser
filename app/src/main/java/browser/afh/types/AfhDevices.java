/*
 * This file is part of AFH Browser.
 *
 * AFH Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AFH Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AFH Browser. If not, see <http://www.gnu.org/licenses/>.
 */

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
