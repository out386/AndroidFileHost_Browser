package browser.afh.tools;

/*
 * Copyright (C) 2016 Harsh Shandilya (MSF-Jarvis)
 */
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

public class Constants {
    public static final String BASE_URL = "https://www.androidfilehost.com/";
    public static final String ENDPOINT = "api";

    /* The number of pages of devices at the time of writing
     * This allows parallel requesting of multiple pages
     * This number does not need to be accurate
     */
    public static final int MIN_PAGES = 9;

    public static final String DID = "https://www.androidfilehost.com/api/?action=developers&did=%s&limit=100";
    public static final String FLID = "https://www.androidfilehost.com/api/?action=folder&flid=%s";
    public static final String TAG = "AFHBrowser";

    public static final String PREF_ASSERT_DATA_COSTS_KEY = "idgaf_for_data_costs_i_eez_reech";
    public static final String PREF_ASSERT_UNOFFICIAL_CLIENT = "its_unofficial";
}
