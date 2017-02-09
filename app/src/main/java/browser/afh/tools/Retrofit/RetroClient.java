package browser.afh.tools.Retrofit;

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

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    private final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    public Dispatcher dispatcher;
    public Retrofit getRetrofit(final Context context, final boolean useOldCache) {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        dispatcher = new Dispatcher();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.dispatcher(dispatcher);
        client.addInterceptor(loggingInterceptor);
        client.addInterceptor(getOfflineCacheInterceptor(context, useOldCache));
        client.addNetworkInterceptor(getShortTermCacheInterceptor());
        client.cache(getCache(context));
        client.readTimeout(180, TimeUnit.SECONDS);
        client.connectTimeout(180, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
    }

    private Cache getCache(Context context) {
        Cache cache = null;
        try {
            cache = new Cache(new File(context.getCacheDir(), "retrofit-cache"), 5 * 1024 * 1024);
        } catch (Exception e) {
            Log.i(Constants.TAG, "getCache: " + e.toString());
        }
        return cache;
    }

    private Interceptor getShortTermCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                        CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(10, TimeUnit.MINUTES)
                        .build();
                return response.newBuilder()
                        .header("cache-control", cacheControl.toString())
                        .removeHeader("pragma")
                        .build();
                }
        };
    }

    private Interceptor getOfflineCacheInterceptor(final Context context, final boolean useOldCache) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // Always use cache if loading the devices list
                // Avoiding isConnectingToInternet to prevent thread troubles
                if (useOldCache || ! ConnectionDetector.networkConnectivity(context)) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }
}
