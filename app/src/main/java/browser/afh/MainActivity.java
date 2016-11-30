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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import browser.afh.data.FindDevices.FragmentInterface;
import browser.afh.fragments.MainFragment;
import browser.afh.tools.ConnectionDetector;
import browser.afh.tools.Constants;
import browser.afh.tools.Utils;

public class MainActivity extends AppCompatActivity implements AppbarScroll, FragmentInterface {
    private Intent searchIntent;
    AppBarLayout appBarLayout;
    TextView headerTV;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        final Context context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        headerTV = (TextView) findViewById(R.id.header_tv);
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_title_home).withIcon(R.drawable.ic_home_black_24px).withIdentifier(0).withDescription(R.string.drawer_desc_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_title_libraries).withIcon(R.drawable.ic_info_black_24px).withIdentifier(1).withDescription(R.string.drawer_desc_libraries).withSelectable(false),
                        new DividerDrawerItem()
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                isConnected = new ConnectionDetector(context).isConnectingToInternet();
            }
        }).start();

        if (isConnected) {
            new BottomDialog.Builder(context)
                    .setTitle(R.string.bottom_dialog_warning_title)
                    .setContent(R.string.bottom_dialog_warning_desc)
                    .setPositiveText(R.string.bottom_dialog_positive_text)
                    .setNegativeTextColorResource(R.color.colorAccent)
                    .onPositive(new BottomDialog.ButtonCallback() {
                                @SuppressLint("CommitPrefEdits")
                                @Override
                                public void onClick(@NonNull BottomDialog bottomDialog) {
                                    bottomDialog.dismiss();
                                    changeFragment(new MainFragment());
                                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
                                    edit.putBoolean(Constants.PREF_ASSERT_DATA_COSTS_KEY, true);
                                    edit.commit();
                                }
                            })
                            .show();
                } else {
                    changeFragment(new MainFragment());
                }

        final MaterialDialog.Builder useLabsVariantDialog = new MaterialDialog.Builder(context)
                .title(R.string.disclaimer_google_play_title)
                .content(R.string.disclaimer_google_play_desc)
                .negativeText(R.string.ok)
                .positiveText(R.string.download)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (Utils.isPackageInstalled(Constants.XDA_LABS_PACKAGE_NAME, getPackageManager())){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.XDA_LABS_APP_PAGE_LINK)));
                        } else {
                            Toast.makeText(context, R.string.err_xda_labs_not_installed, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.XDA_LABS_DOWNLOAD_PAGE)));
                        }

                    }
                });
        boolean its_unofficial = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_ASSERT_UNOFFICIAL_CLIENT, false);
        if (!its_unofficial){
            new MaterialDialog.Builder(context)
                    .title(R.string.unofficial_disclaimer_title)
                    .content(R.string.unofficial_disclaimer_text)
                    .neutralText(R.string.file_dialog_neutral_button_label)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.PREF_ASSERT_UNOFFICIAL_CLIENT,true).apply();
                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.PREF_ASSERT_UNOFFICIAL_CLIENT,true).apply();
                            if (BuildConfig.PLAY_COMPTABILE) useLabsVariantDialog.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchIntent = new Intent(Constants.INTENT_SEARCH);
                searchIntent.putExtra(Constants.INTENT_SEARCH_QUERY, query);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchIntent = new Intent(Constants.INTENT_SEARCH);
                searchIntent.putExtra(Constants.INTENT_SEARCH_QUERY, newText);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(searchIntent);
                return true;
            }
        });

        return true;
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
        if (message != null)
            headerTV.setVisibility(View.VISIBLE);
        else {
            headerTV.setVisibility(View.GONE);
            return;
        }
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
    @Override
    public void onSuperBack() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(Constants.INTENT_BACK);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(backIntent);
    }
}
