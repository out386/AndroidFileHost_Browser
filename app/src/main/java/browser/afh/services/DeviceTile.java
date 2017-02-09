package browser.afh.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import browser.afh.MainActivity;
import browser.afh.R;
import browser.afh.tools.Constants;
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
            tile.setState(Tile.STATE_UNAVAILABLE);
            Toast.makeText(this, getApplicationContext().getString(R.string.tile_unavailable), Toast.LENGTH_SHORT).show();
        } else {
            if (tile.getState() == Tile.STATE_UNAVAILABLE)
                tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(label);
            tile.updateTile();
        }

        super.onClick();
    }

}
