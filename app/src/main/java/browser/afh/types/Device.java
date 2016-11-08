package browser.afh.types;

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

import java.io.Serializable;

public class Device implements Serializable{
    public String did;
    public String manufacturer;
    public String device_name;
    public Device( String did, String manufacturer, String device_name) {
        this.did = did;
        this.manufacturer = manufacturer;
        this.device_name = device_name;
    }
}
