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

package browser.afh.types;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AfhDevelopers {
    @SerializedName("DATA")
    public final List<Developer> data;
	
	public AfhDevelopers(List<Developer> data) {
        this.data = data;
    }

    public class Developer {
        @SerializedName("screenname")
        public String screenname;
        @SerializedName("flid")
        public String flid;
        @SerializedName("name")
        public String name;

    }
}