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

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import browser.afh.activities.PreferencesActivity;
import browser.afh.data.FindDevices.AppbarScroll;
import browser.afh.data.FindDevices.FragmentRattach;
import browser.afh.fragments.MainFragment;
import browser.afh.tools.Constants;

public class MainActivity extends AppCompatActivity implements AppbarScroll, FragmentRattach {

    AppBarLayout appBarLayout;
    TextView headerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        final Context context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        headerTV = (TextView) findViewById(R.id.header_tv);
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
        boolean isFirstInternetWarning = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("idgaf_for_data_costs_i_eez_reech", false);
        if (!isFirstInternetWarning) {
            if (checkIfMobileData()) {
                new BottomDialog.Builder(this)
                        .setTitle(R.string.bottom_dialog_warning_title)
                        .setContent(R.string.bottom_dialog_warning_desc)
                        .setPositiveText(R.string.bottom_dialog_positive_text)
                        .setNegativeText(R.string.bottom_dialog_negative_text)
                        .setNegativeTextColorResource(R.color.colorAccent)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @SuppressLint("CommitPrefEdits")
                            @Override
                            public void onClick(@NonNull BottomDialog bottomDialog) {
                                bottomDialog.dismiss();
                                changeFragment(new MainFragment());
                                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
                                edit.putBoolean("idgaf_for_data_costs_i_eez_reech", true);
                                edit.commit();
                            }
                        })
                        .onNegative(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog bottomDialog) {
                                bottomDialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            } else {
                changeFragment(new MainFragment());
            }
        } else {
            changeFragment(new MainFragment());
        }

        boolean its_unofficial = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("its_unofficial", false);
        if (!its_unofficial){
            new MaterialDialog.Builder(context)
                    .title(R.string.unofficial_disclaimer_title)
                    .content(R.string.unofficial_disclaimer_text)
                    .neutralText(R.string.file_dialog_neutral_button_label)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("its_unofficial",true).apply();
                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("its_unofficial",true).apply();
                        }
                    })
                    .show();
        }

    }

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .commit();
    }

    public boolean checkIfMobileData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }

    @Override
    public void expand() {
        appBarLayout.setExpanded(true, true);
    }
    @Override
    public void collapse() {
        appBarLayout.setExpanded(false, true);
    }
    @Override
    public void setText(String message) {
        headerTV.setText(message);
    }
    @Override
    public String getText() {
        return headerTV.getText().toString();
    }
    @Override
    public void reattach() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment current = fragmentManager.findFragmentById(R.id.mainFrame);
        if (current instanceof MainFragment) {
            fragmentManager.beginTransaction()
                    .detach(current)
                    .attach(current)
                    .commit();
            changeFragment(current);
        }
    }
}
