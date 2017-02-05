package browser.afh.types;

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

import com.google.gson.annotations.SerializedName;

import browser.afh.tools.Utils;

public class AfhFiles
{
    @SerializedName("name")
    public String name;
    @SerializedName("url")
    public String url;
    @SerializedName("file_size")
    public String file_size;
    @SerializedName("upload_date")
    public String upload_date;
    @SerializedName("downloads")
    public int downloads;
    public String screenname;
}