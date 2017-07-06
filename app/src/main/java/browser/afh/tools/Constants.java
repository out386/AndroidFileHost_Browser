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

package browser.afh.tools;

public class Constants {
    public static final String BASE_URL = "https://www.androidfilehost.com/";
    public static final String ENDPOINT = "api";

    /* The number of pages of devices at the time of writing
     * This allows parallel requesting of multiple pages
     * This number does not need to be accurate
     */
    public static final int MIN_PAGES = 9;
    public static final long ANIM_DURATION = 500;

    public static final String TAG = "AFHBrowser";

    public static final String PREF_ASSERT_UNOFFICIAL_CLIENT = "its_unofficial";

    public static final String INTENT_SEARCH = "search";
    public static final String INTENT_SNACKBAR = "snackbar";

    public static final String KEY_FILES_LIST = "filesList";
    public static final String KEY_LAST_FRAGMENT = "lastFragment";

    public static final String TAG_FILES_FRAGMENT = "filesFragment";
    public static final String TAG_DEVICES_FRAGMENT = "devicesFragment";
    public static final String TAG_SETTINGS_FRAGMENT = "settingsFragment";

    // This is to let users know that the Play Store version is tied down
    public static final String XDA_LABS_PACKAGE_NAME = "com.xda.labs";
    public static final String XDA_LABS_APP_PAGE_LINK = "https://labs.xda-developers.com/store/apps/rom.stalker";
    public static final String XDA_LABS_DOWNLOAD_PAGE = "https://www.xda-developers.com/xda-labs/";

    public static final String EXTRA_SEARCH_QUERY = "searchQuery";
    public static final String EXTRA_DEVICE_ID = "deviceID";
    public static final String EXTRA_SNACKBAR_MESSAGE = "snackbarMessage";

}
