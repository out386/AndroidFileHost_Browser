/*
 * Copyright (C) 2016 Harsh Shandilya (MSF-Jarvis)
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hugo.weaving.DebugLog;

public class ConnectionDetector {
    private final String TAG = getClass().getCanonicalName();

    @DebugLog
    public static boolean isConnectingToInternet(Context context) {
        if (networkConnectivity(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(
                        "https://www.androidfilehost.com").openConnection());
                urlc.setRequestProperty("User-Agent", "AFHBrowser");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(30000);
                urlc.setReadTimeout(10000);
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e) {
                Log.d(Constants.TAG, String.format("isConnectingToInternet: %s",e.toString()));
                return false;
            }
        } else
            return false;
    }

    public static boolean networkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}