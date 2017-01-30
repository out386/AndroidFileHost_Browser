package browser.afh.tools;

import java.util.Comparator;

import browser.afh.types.AfhFiles;
import browser.afh.types.DeviceData;

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
    public static final Comparator <AfhFiles> byUploadDate = new Comparator<AfhFiles>() {
        @Override
        public int compare(AfhFiles f1, AfhFiles f2) {
            if (f1 != null && f2 != null && f1.hDate != null && f2.hDate != null)
                return -(f1.hDate.compareTo(f2.hDate));
            return 0;
        }
    };
    public static final Comparator <AfhFiles> byFileName = new Comparator<AfhFiles>() {
        @Override
        public int compare(AfhFiles f1, AfhFiles f2) {
            if (f1 != null && f2 != null && f1.filename != null && f2.filename != null)
                return (f1.filename.compareToIgnoreCase(f2.filename));
            return 0;
        }
    };
    public static final Comparator <DeviceData> byManufacturer = new Comparator<DeviceData>() {
        @Override
        public int compare(DeviceData f1, DeviceData f2) {
            if (f1 != null && f2 != null && f1.manufacturer != null && f2.manufacturer != null)
                return (f1.manufacturer.compareToIgnoreCase(f2.manufacturer));
            return 0;
        }
    };
}
