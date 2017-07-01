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

package browser.afh.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import browser.afh.MainActivity;
import browser.afh.R;
import browser.afh.tools.Prefs;


@TargetApi(Build.VERSION_CODES.N)
public class DeviceTile extends TileService {

    @Override
    public void onClick() {
        String e = new Prefs(getApplicationContext()).get("device_id", null);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("device_id", e);
        startActivityAndCollapse(intent);


        /* This should be done in onStartListening for proper working,
         * but trying to avoid reading from  sharedprefs every time the user opens QS
         */

        final Tile tile = getQsTile();
        String label = new Prefs(getApplicationContext()).get("device_name", null);

        if (label == null) {
            Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.tile_unavailable),
                    Toast.LENGTH_SHORT).show();
        } else {
            tile.setLabel(label);
            tile.updateTile();
        }

        super.onClick();
    }

}
