package browser.afh;

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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import browser.afh.activities.PreferencesActivity;
import browser.afh.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        final Context context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_title_home).withIcon(R.drawable.ic_home_black_24px).withIdentifier(0).withDescription(R.string.drawer_desc_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_title_libraries).withIcon(R.drawable.ic_info_black_24px).withIdentifier(1).withDescription(R.string.drawer_desc_libraries).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_title_settings).withIcon(R.drawable.ic_settings_black_24px).withIdentifier(2).withDescription(R.string.drawer_desc_settings).withSelectable(false)
                )
                .withCloseOnClick(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 0) {
                            changeFragment(new MainFragment());
                        } else if (drawerItem.getIdentifier() == 1) {
                            new LibsBuilder()
                                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                    .withAboutAppName(getString(R.string.app_name))
                                    .withAboutIconShown(true)
                                    .withAboutVersionShown(true)
                                    .start(context);
                        } else if (drawerItem.getIdentifier() == 2) {
                            Intent intent = new Intent(context, PreferencesActivity.class);
                            startActivity(intent);
                        }
                        return false;
                    }
                })
                .build();
        changeFragment(new MainFragment());

    }
    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .commit();
    }
}
