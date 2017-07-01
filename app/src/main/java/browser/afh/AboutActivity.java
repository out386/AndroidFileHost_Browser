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

package browser.afh;

import android.content.Intent;
import android.net.Uri;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.model.MaterialAboutTitleItem;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected MaterialAboutList getMaterialAboutList() {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
        appCardBuilder.title(getString(R.string.app_name));
        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(getString(R.string.app_name))
                .icon(R.mipmap.ic_launcher)
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_version_title)
                .subText(BuildConfig.VERSION_NAME)
                .icon(R.drawable.ic_info)
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_licenses_title)
                .icon(R.drawable.ic_licenses)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        LibsConfiguration.getInstance().setItemAnimator(new SlideDownAlphaAnimator());
                        new LibsBuilder()
                                .withFields(R.string.class.getFields())
                                .withActivityTitle(getString(R.string.app_name))
                                .withActivityTheme(R.style.AppTheme_MaterialAboutActivity)
                                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                .activity(AboutActivity.this);
                    }
                })
                .build());
        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.about_activity_author_title_1);
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_author_title_1)
                .subText(R.string.about_activity_author_desc)
                .icon(R.drawable.ic_person)
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_github_title)
                .subText(R.string.about_activity_github_subtitle_1)
                .icon(R.drawable.ic_github)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://github.com/out386/"));
                        startActivity(i);
                    }
                })
                .build());

        MaterialAboutCard.Builder author2CardBuilder = new MaterialAboutCard.Builder();
        author2CardBuilder.title(R.string.about_activity_author_title_2);
        author2CardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_author_title_2)
                .subText(R.string.about_activity_author_desc)
                .icon(R.drawable.ic_person)
                .build());

        author2CardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_github_title)
                .subText(R.string.about_activity_github_subtitle_2)
                .icon(R.drawable.ic_github)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://github.com/MSF-Jarvis/"));
                        startActivity(i);
                    }
                })
                .build());


            author2CardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_twitter_title)
                .subText(R.string.about_activity_twitter_subtitle_2)
                .icon(R.drawable.ic_twitter)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://twitter.com/MSF_Jarvis"));
                        startActivity(i);
                    }
                })
                .build());
        return new MaterialAboutList.Builder()
                .addCard(appCardBuilder.build())
                .addCard(authorCardBuilder.build())
                .addCard(author2CardBuilder.build())
                .build();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.about_activity_title);
    }
}
