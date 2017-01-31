package browser.afh.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import browser.afh.MainActivity;

@TargetApi(Build.VERSION_CODES.N)
public class AfhBrowserTile extends TileService {
    @Override
    public void onClick() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
