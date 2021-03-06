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

package browser.afh.tools.Retrofit;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

import browser.afh.BuildConfig;
import browser.afh.tools.ConnectionDetector;
import browser.afh.tools.Constants;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    private static final Dispatcher dispatcher = new Dispatcher();
    private static Retrofit retrofitForceCache;
    private static Retrofit retrofitNoForceCache;
    private static ApiInterface apiForceCache;
    private static ApiInterface apiNoForceCache;
    private static Cache cache;
    private static Interceptor removeHeadersInterceptor;
    private static boolean isCurrentlyFast = false;


    private static Retrofit getRetrofit(final Context context, final boolean useOldCache) {
        if (useOldCache && retrofitForceCache != null)
            return retrofitForceCache;
        else if (!useOldCache && retrofitNoForceCache != null)
            return retrofitNoForceCache;

        if (!BuildConfig.DEBUG)
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        else
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(getOfflineCacheInterceptor(context, useOldCache))
                .addNetworkInterceptor(removeHeaders())
                .cache(getCache(context))
                .readTimeout(4, TimeUnit.SECONDS)
                .connectTimeout(4, TimeUnit.SECONDS);

        if (useOldCache) {
            retrofitForceCache = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
            return retrofitForceCache;
        }
        retrofitNoForceCache = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        return retrofitNoForceCache;
    }

    private static Cache getCache(Context context) {
        if (cache != null)
            return cache;

        try {
            cache = new Cache(new File(context.getCacheDir(), "retrofit-cache"), 20 * 1024 * 1024);
        } catch (Exception e) {
            Log.i(Constants.TAG, "getCache: " + e.toString());
        }
        return cache;
    }

    private static Interceptor removeHeaders() {
        if (removeHeadersInterceptor == null) {
            removeHeadersInterceptor = chain -> {
                Response response = chain.proceed(chain.request());
                return response.newBuilder()
                        .removeHeader("pragma")
                        .removeHeader("cache-control")
                        .build();
            };
        }
        return removeHeadersInterceptor;
    }

    private static Interceptor getOfflineCacheInterceptor(final Context context, final boolean useOldCache) {
        return chain -> {
            Request request = chain.request();

            // Avoiding isConnectingToInternet to prevent thread troubles
            if (useOldCache || !(new ConnectionDetector()).networkConnectivity(context)) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(30, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            return chain.proceed(request);
        };
    }

    public static void cancelRequests() {
        dispatcher.cancelAll();
    }

    public static ApiInterface getApi(Context context, boolean useOldCache) {
        boolean isNowFast = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("retroEnableFast", false);
        if (isNowFast != isCurrentlyFast) {
            isCurrentlyFast = isNowFast;
            retrofitForceCache = null;
            retrofitNoForceCache = null;
            apiForceCache = null;
            apiNoForceCache = null;
            if (isNowFast)
                dispatcher.setMaxRequestsPerHost(10);
            else
                dispatcher.setMaxRequestsPerHost(5);
        }

        if (useOldCache) {
            if (apiForceCache == null)
                apiForceCache = getRetrofit(context, true).create(ApiInterface.class);
            return apiForceCache;
        }

        if (apiNoForceCache == null)
            apiNoForceCache = getRetrofit(context, false).create(ApiInterface.class);
        return apiNoForceCache;
    }
}
