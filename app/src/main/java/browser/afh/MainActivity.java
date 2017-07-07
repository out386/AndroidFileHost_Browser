/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386) and Harsh Shandilya (MSF-Jarvis)
 *
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

package browser.afh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.lapism.searchview.SearchView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import browser.afh.data.FindDevices.AppbarScroll;
import browser.afh.data.FindDevices.FragmentInterface;
import browser.afh.data.FindDevices.HSShortutInterface;
import browser.afh.fragments.DevicesFragment;
import browser.afh.fragments.FilesFragment;
import browser.afh.fragments.SettingsFragment;
import browser.afh.tools.ConnectionDetector;
import browser.afh.tools.Constants;
import browser.afh.tools.Prefs;
import browser.afh.tools.Utils;
import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;

import static browser.afh.tools.Utils.isPackageInstalled;

public class MainActivity extends AppCompatActivity implements AppbarScroll, FragmentInterface,
        HSShortutInterface, ColorChooserDialog.ColorCallback {
    AppBarLayout appBarLayout;
    TextView headerTV;
    private Intent searchIntent;
    private Prefs prefs;
    private Drawer drawer;
    private Snackbar snackbar;
    private FrameLayout frame;
    private List<Long> drawerPositions = new ArrayList<>();
    private AsyncTask checkConnectivity;
    private AppUpdater appUpdater;
    private SearchView searchView;

    private BroadcastReceiver snackbarMakeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (frame == null)
                return;
            int message = intent.getIntExtra(Constants.EXTRA_SNACKBAR_MESSAGE, -1);
            if (message > -1) {
                snackbar = Snackbar.make(frame, getResources().getString(message), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        prefs = new Prefs(getApplicationContext());
        String deviceID = getIntent().getStringExtra(Constants.EXTRA_DEVICE_ID);
        if (!BuildConfig.DEBUG) {
            // This will crash the app if a debug build tries to use Crashlytics.log
            Fabric.with(this, new Crashlytics());
        }
        searchView = (SearchView) findViewById(R.id.searchView);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        headerTV = (TextView) findViewById(R.id.header_tv);

        frame = (FrameLayout) findViewById(R.id.mainFrame);
        IntentFilter snackbarMakeFilter = new IntentFilter(Constants.INTENT_SNACKBAR);
        LocalBroadcastManager.getInstance(this).registerReceiver(snackbarMakeReceiver, snackbarMakeFilter);

        findViewById(R.id.app_bar_bg).setBackgroundColor(Utils.getPrefsColour(1, getApplicationContext()));
        getWindow().setNavigationBarColor(Utils.getPrefsColour(1, getApplicationContext()));

        updatesCheck(this);
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(new ColorDrawable(Utils.getPrefsColour(1, getApplicationContext())))
                .withProfileImagesVisible(false)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(getString(R.string.app_name)).withEmail
                                (BuildConfig.VERSION_NAME))
                .withCurrentProfileHiddenInList(true)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_title_home)
                                .withIcon(R.drawable.ic_home)
                                .withIdentifier(0)
                                .withIconColor(Utils.getPrefsColour(2, getApplicationContext()))
                                .withSelectedIconColor(Utils.getPrefsColour(1, getApplicationContext()))
                                .withIconTintingEnabled(true)
                                .withSelectedTextColor(Utils.getPrefsColour(1, getApplicationContext()))
                                .withDescription(R.string.drawer_desc_home),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_title_info)
                                .withIcon(R.drawable.ic_info)
                                .withIdentifier(1)
                                .withIconColor(Utils.getPrefsColour(2, getApplicationContext()))
                                .withSelectedIconColor(Utils.getPrefsColour(1, getApplicationContext()))
                                .withIconTintingEnabled(true)
                                .withSelectedTextColor(Utils.getPrefsColour(1, getApplicationContext()))
                                .withDescription(R.string.drawer_desc_info)
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_title_settings)
                                .withIcon(R.drawable.ic_settings)
                                .withIdentifier(2)
                                .withIconColor(Utils.getPrefsColour(2, getApplicationContext()))
                                .withSelectedIconColor(Utils.getPrefsColour(1, getApplicationContext()))
                                .withIconTintingEnabled(true)
                                .withSelectedTextColor(Utils.getPrefsColour(1, getApplicationContext()))
                                .withDescription(R.string.drawer_desc_settings)
                )
                .withCloseOnClick(true)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    long index = drawerItem.getIdentifier();

                    if (drawerPositions.size() > 0 && index == drawerPositions.get(drawerPositions.size() - 1))
                        return false;

                    if (index == 0) {
                        changeFragment(new DevicesFragment());
                        drawerPositions.add(index);
                    } else if (index == 1) {
                        // Not adding drawerPositions here because index == 1 is another activity
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    } else if (index == 2) {
                        changeFragment(new SettingsFragment());
                        drawerPositions.add(index);
                    }
                    return false;
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (searchView != null && searchView.isSearchOpen())
                            searchView.close(true);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();

        final MaterialDialog.Builder useLabsVariantDialog = new MaterialDialog.Builder(getApplicationContext())
                .title(R.string.disclaimer_google_play_title)
                .content(R.string.disclaimer_google_play_desc)
                .negativeText(R.string.ok)
                .positiveText(R.string.download)
                .onNegative((dialog, which) -> dialog.dismiss())
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    if (isPackageInstalled(Constants.XDA_LABS_PACKAGE_NAME, getPackageManager())) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.XDA_LABS_APP_PAGE_LINK)));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.err_xda_labs_not_installed, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.XDA_LABS_DOWNLOAD_PAGE)));
                    }

                });
        boolean its_unofficial = prefs.get(Constants.PREF_ASSERT_UNOFFICIAL_CLIENT, false);
        if (!its_unofficial) {
            new MaterialDialog.Builder(this)
                    .title(R.string.unofficial_disclaimer_title)
                    .content(R.string.unofficial_disclaimer_text)
                    .neutralText(R.string.file_dialog_neutral_button_label)
                    .onNeutral((dialog, which) -> {
                        dialog.dismiss();
                        prefs.put(Constants.PREF_ASSERT_UNOFFICIAL_CLIENT, true);
                    })
                    .dismissListener(dialogInterface -> {
                        prefs.put(Constants.PREF_ASSERT_UNOFFICIAL_CLIENT, true);
                        if (BuildConfig.PLAY_COMPATIBLE) useLabsVariantDialog.show();
                    })
                    .positiveColor(Utils.getPrefsColour(2, this))
                    .show();
        }

        checkConnectivity = new CheckConnectivity(this).execute();

        if (deviceID != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.EXTRA_DEVICE_ID, deviceID);
            Fragment mainFragment = new FilesFragment();
            mainFragment.setArguments(bundle);
            changeFragment(mainFragment, true);
            return;
        }

        searchView.setHint(getResources().getString(R.string.search_hint));
        searchView.setFocusable(false);
        searchView.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!BuildConfig.DEBUG) {
                    Answers.getInstance().logSearch(new SearchEvent()
                            .putQuery(query));
                }
                searchView.close(true);
                searchIntent = new Intent(Constants.INTENT_SEARCH);
                searchIntent.putExtra(Constants.EXTRA_SEARCH_QUERY, query);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchIntent = new Intent(Constants.INTENT_SEARCH);
                searchIntent.putExtra(Constants.EXTRA_SEARCH_QUERY, newText);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(searchIntent);
                return true;
            }
        });
        searchView.setOnMenuClickListener(() -> drawer.openDrawer());


        if (savedInstanceState == null)
            changeFragment(new DevicesFragment());
    }

    public void changeFragment(Fragment fragment) {
        changeFragment(fragment, false);
    }

    public void changeFragment(Fragment fragment, boolean forceAsFirstFragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();

        if (snackbar != null)
            snackbar.dismiss();

        if (fragment instanceof DevicesFragment) {
            setText(null);
            appBarLayout.setExpanded(true, true);

            for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++)
                getFragmentManager().popBackStack();

            drawerPositions.clear();
            drawerPositions.add(0L);
        } else {
            if (fragment instanceof FilesFragment) {
                if (! forceAsFirstFragment) {
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction
                            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out,
                                    R.animator.fade_in_enter, R.animator.fade_out_exit);
                } else {
                    for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++)
                        getFragmentManager().popBackStack();
                }


            } else {
                if (fragment instanceof SettingsFragment) {
                    showSearch(false, true);
                    appBarLayout.setExpanded(true, true);
                }
                fragmentTransaction.addToBackStack(null);
                setText(null);
            }
            showSearch(false, false);
        }

        fragmentTransaction
                .replace(R.id.mainFrame, fragment)
                .commit();
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
    public String getText() {
        return headerTV.getText().toString();
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
    public void onBackPressed() {
        if (snackbar != null)
            snackbar.dismiss();
        int size = drawerPositions.size();

        if (size > 1) {
            long pos = drawerPositions.get(size - 2);
            drawerPositions.remove(size - 1);
            drawer.setSelection(pos);
        }
        super.onBackPressed();
    }

    @DebugLog
    public void showSearch(boolean show, boolean isAnim) {
        if (!isAnim) {
            searchView.setVisibility(show ? View.VISIBLE : View.GONE);
            return;
        }
        if (show) {
            searchView.setTranslationY(-searchView.getHeight());
            searchView.setAlpha(0);
            searchView.setVisibility(View.VISIBLE);
            searchView.animate()
                    .setDuration(Constants.ANIM_DURATION)
                    .translationY(0)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            searchView.setVisibility(View.VISIBLE);

                        }
                    });

        } else {
            searchView.setTranslationY(0);
            searchView.setAlpha(1);
            searchView.setVisibility(View.VISIBLE);
            searchView.animate()
                    .setDuration(Constants.ANIM_DURATION)
                    .translationY(-searchView.getHeight())
                    .alpha(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            searchView.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void setShortcut(String did, String manufacturer, String deviceName) {
        //Home screen shortcut for favourite device

        if (Build.VERSION.SDK_INT < 25)
            return;
        ShortcutManager sM = getSystemService(ShortcutManager.class);
        sM.removeAllDynamicShortcuts();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Constants.EXTRA_DEVICE_ID, did);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "shortcut1")
                .setIntent(intent)
                .setLongLabel(manufacturer + " " + deviceName)
                .setShortLabel(deviceName)
                .setIcon(Icon.createWithResource(this, R.drawable.ic_device_placeholder))
                .build();
        sM.setDynamicShortcuts(Collections.singletonList(shortcut));
    }

    @DebugLog
    public void updatesCheck(Context context) {
        appUpdater = new AppUpdater(context)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("out386", "AndroidFileHost_Browser")
                .showAppUpdated(false)
                .setDisplay(Display.DIALOG);
        appUpdater.start();
    }

    private static class CheckConnectivity extends AsyncTask<Void, Void, Boolean> {
        WeakReference<Context> contextReference;

        CheckConnectivity(Context context) {
            contextReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Void... v) {
            Context context = contextReference.get();
            return context != null && new ConnectionDetector().isConnectingToInternet(context);
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            Context context = contextReference.get();
            if (!isConnected && !((Activity) context).isFinishing()) {
                new BottomDialog.Builder(context)
                        .setTitle(R.string.bottom_dialog_warning_title)
                        .setContent(R.string.bottom_dialog_warning_desc)
                        .setPositiveText(R.string.bottom_dialog_positive_text)
                        .setNegativeTextColorResource(Utils.getPrefsColour(2,context))
                        .onPositive(BottomDialog::dismiss)
                        .show();
            }
        }
    }

    @Override
    public void showDevice(String did, int position) {
        if (did == null) {
            Snackbar.make(frame, "Invalid device selected", Snackbar.LENGTH_INDEFINITE);
            return;
        }

        FilesFragment filesFragment = new FilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_DEVICE_ID, did);
        filesFragment.setArguments(bundle);

        changeFragment(filesFragment);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(snackbarMakeReceiver);
        checkConnectivity.cancel(true);
        appUpdater.stop();
        super.onDestroy();
    }

    public void showColourDialog(int type) {
        switch (type) {
            case 1:
                new ColorChooserDialog.Builder(this, R.string.dialog_colour_primary_title)
                        .preselect(Utils.getPrefsColour(1, getApplicationContext()))
                        .show();
                break;
            case 2:
                new ColorChooserDialog.Builder(this, R.string.dialog_colour_accent_title)
                        .accentMode(true)
                        .preselect(Utils.getPrefsColour(2, getApplicationContext()))
                        .show();
                break;
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        if (R.string.dialog_colour_primary_title == dialog.getTitle()) {
            prefs.put(Constants.PREFS_COLOR_PRIMARY, String.valueOf(color));
            Utils.resetColours(1);
        } else if (R.string.dialog_colour_accent_title == dialog.getTitle()) {
            prefs.put(Constants.PREFS_COLOR_ACCENT, String.valueOf(color));
            Utils.resetColours(2);
        }
        recreate();
    }


}
