package browser.afh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.model.MaterialAboutTitleItem;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import browser.afh.BuildConfig;
import browser.afh.R;

public class AboutActivity extends MaterialAboutActivity {
    Context context;

    @Override
    protected MaterialAboutList getMaterialAboutList() {
        context = getApplicationContext();
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
        appCardBuilder.title(getString(R.string.app_name));
        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(getString(R.string.app_name))
                .icon(R.mipmap.ic_launcher)
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_version_title)
                .subText(BuildConfig.VERSION_NAME)
                .icon(R.drawable.ic_info_black_24px)
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_licenses_title)
                .icon(R.drawable.ic_licenses_black_24px)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
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
                .icon(R.drawable.ic_person_black_24px)
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_github_title)
                .subText(R.string.about_activity_github_subtitle_1)
                .icon(R.drawable.ic_github)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://github.com/out386/AndroidFileHost_Browser"));
                        startActivity(i);
                    }
                })
                .build());

        MaterialAboutCard.Builder author2CardBuilder = new MaterialAboutCard.Builder();
        author2CardBuilder.title(R.string.about_activity_author_title_2);
        author2CardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_author_title_2)
                .subText(R.string.about_activity_author_desc)
                .icon(R.drawable.ic_person_black_24px)
                .build());

        author2CardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_github_title)
                .subText(R.string.about_activity_github_subtitle_2)
                .icon(R.drawable.ic_github)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://github.com/MSF-Jarvis/AndroidFileHost_Browser"));
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
