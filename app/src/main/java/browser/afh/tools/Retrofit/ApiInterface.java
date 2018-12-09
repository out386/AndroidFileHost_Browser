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

package browser.afh.tools.Retrofit;

import browser.afh.tools.Constants;
import browser.afh.types.AfhDevices;
import browser.afh.types.AfhDevelopers;
import browser.afh.types.AfhFolders;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {
    @Headers({
            "User-Agent: " + Constants.USER_AGENT
    })
    @GET(Constants.ENDPOINT)
    Call<AfhDevelopers> getDevelopers(
            @Query("action") String action,
            @Query("did") String page,
            @Query("limit") int limit);

    @Headers({
            "User-Agent: " + Constants.USER_AGENT
    })
    @GET(Constants.ENDPOINT)
    Call<AfhDevices> getDevices(
            @Query("action") String action,
            @Query("page") int page,
            @Query("limit") int limit);

    @Headers({
            "User-Agent: " + Constants.USER_AGENT
    })
    @GET(Constants.ENDPOINT)
    Call<AfhFolders> getFolderContents(
            @Query("action") String action,
            @Query("flid") String flid,
            @Query("limit") int limit);
}
