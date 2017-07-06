/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386) and Harsh Shandilya (MSF-Jarvis)
 *
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

package browser.afh.tools;

import android.content.pm.PackageManager;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.text.DecimalFormat;

public class Utils {
    public static String sizeFormat(long size) {
        if (size <= 0)
            return null;
        float newSize = size;
        String unit = " B";
        if (newSize > 1024) {
            unit = " KiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " MiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " GiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " TiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = ". Wrong size.";
            newSize = 0;
        }
        return (new DecimalFormat("#0.00").format(newSize) + unit);
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
