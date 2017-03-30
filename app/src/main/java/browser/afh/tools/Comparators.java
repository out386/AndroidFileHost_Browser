package browser.afh.tools;

import java.util.Comparator;

import browser.afh.types.AfhDevices;
import browser.afh.types.AfhFolders.Files;

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

public class Comparators {
    public static final Comparator <Files> byUploadDate = new Comparator<Files>() {
        @Override
        public int compare(Files f1, Files f2) {
            if (f1 != null && f2 != null && f1.upload_date != null && f2.upload_date != null)
                return -(f1.upload_date.compareTo(f2.upload_date));
            return 0;
        }
    };
    public static final Comparator <Files> byFileName = new Comparator<Files>() {
        @Override
        public int compare(Files f1, Files f2) {
            if (f1 != null && f2 != null && f1.name != null && f2.name != null)
                return (f1.name.compareToIgnoreCase(f2.name));
            return 0;
        }
    };
    public static final Comparator <AfhDevices.Device> byManufacturer = new Comparator<AfhDevices.Device>() {
        @Override
        public int compare(AfhDevices.Device f1, AfhDevices.Device f2) {
            if (f1 != null && f2 != null && f1.manufacturer != null && f2.manufacturer != null)
                return (f1.manufacturer.compareToIgnoreCase(f2.manufacturer));
            return 0;
        }
    };
}
