package browser.afh.tools;

import java.util.Comparator;

import browser.afh.types.AfhFiles;
import browser.afh.types.Device;

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
    public static Comparator <AfhFiles> byUploadDate = new Comparator<AfhFiles>() {
        @Override
        public int compare(AfhFiles f1, AfhFiles f2) {
            return -(f1.file_size.compareTo(f2.file_size));
        }
    };
    public static Comparator <AfhFiles> byFileName = new Comparator<AfhFiles>() {
        @Override
        public int compare(AfhFiles f1, AfhFiles f2) {
            return (f1.filename.compareToIgnoreCase(f2.filename));
        }
    };
    public static Comparator <Device> byManufacturer = new Comparator<Device>() {
        @Override
        public int compare(Device f1, Device f2) {
            return (f1.manufacturer.compareToIgnoreCase(f2.manufacturer));
        }
    };
}
