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
    public void onTileAdded(){
        final Tile tile = getQsTile();
        tile.setLabel(new Prefs(getApplicationContext()).get("device_name", "????"));
        if (tile.getLabel() == "????") tile.setState(Tile.STATE_UNAVAILABLE);
        Toast.makeText(this, getApplicationContext().getString(R.string.tile_unavailable), Toast.LENGTH_SHORT).show();
        super.onTileAdded();
    }

    @Override
    public void onClick(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("device_id",new Prefs(getApplicationContext()).get("device_id",0));
        startActivity(intent);
    }

}
