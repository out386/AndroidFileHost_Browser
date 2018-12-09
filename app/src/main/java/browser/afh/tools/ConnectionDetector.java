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
import android.util.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class ConnectionDetector {

    private static final int MAX_RETRIES = 5;

    public Pair<Boolean, Boolean> isConnectingToInternet(Context context) {
        boolean isAfhAvailable;
        boolean isGoogleAvailable = isConnectingToHost(
                context, Constants.CONNECTIVITY_CHECK_GOOGLE, 204, 1);
        // Highly unlikely that Google will be unavailable and AFH will be, but IDK if gstatic works in China, so checking anyway
        isAfhAvailable = isConnectingToHost(
                context, Constants.BASE_URL, 200, 1);
        return new Pair<>(isGoogleAvailable, isAfhAvailable);
    }

    private boolean isConnectingToHost(Context context, String hostName, int code, int tryNumber) {
        if (networkConnectivity(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(hostName).openConnection());
                urlc.setRequestProperty("User-Agent", "AFHBrowser");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(4000);
                urlc.setReadTimeout(4000);
                urlc.connect();
                return urlc.getResponseCode() == code;
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException && tryNumber < MAX_RETRIES)
                    return isConnectingToHost(context, hostName, code, tryNumber + 1);
                Log.d(Constants.TAG, String.format("isConnectingToInternet: %s", e.toString()));
                return false;
            }
        } else
            return false;
    }

    public boolean networkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}