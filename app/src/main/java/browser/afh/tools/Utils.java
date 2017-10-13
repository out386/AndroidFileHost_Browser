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

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import java.text.DecimalFormat;

import browser.afh.R;

public class Utils {
    private static int colorPrimary = -1;
    private static int colorAccent = -1;

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

    public static int getPrefsColour(int type, Context context) {
        Prefs prefs = new Prefs(context);
        switch (type) {
            case 1:
                if (colorPrimary == -1) {
                    colorPrimary = Integer.parseInt(prefs
                            .get(Constants.PREFS_COLOR_PRIMARY, String.valueOf(
                                    ContextCompat.getColor(context, R.color.colorPrimary))));
                }
                return colorPrimary;
            case 2:
                if (colorAccent == -1) {
                    colorAccent = Integer.parseInt(prefs
                            .get(Constants.PREFS_COLOR_ACCENT, String.valueOf(
                                    ContextCompat.getColor(context, R.color.colorAccent))));
                }
                return colorAccent;
            default:
                return 0xffffff;
        }
    }

    public static void resetColours(int type) {
        switch (type) {
            case 1:
                colorPrimary = -1;
                break;
            case 2:
                colorAccent = -1;
                break;
        }
    }

    public static void tintCheckbox(CheckBox checkBox, Context context) {
        int[] [] states = {
                {android.R.attr.state_checked},
                {-android.R.attr.state_checked}
        };
        int [] colours = {Utils.getPrefsColour(2, context), 0xaa000000};

        checkBox.setButtonTintList(new ColorStateList(states, colours));
    }

    public static SpannableString generateSpannable(String message, int number, int spaceBeforeNumber) {
        SpannableString text = new SpannableString(
                String.format(message, number));

        int numberSize = String.valueOf(number).length() + spaceBeforeNumber;
        text.setSpan(new RelativeSizeSpan(1.5f), 0 , numberSize, 0);
        text.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                numberSize + 1, text.length(), 0);
        return text;
    }
}
