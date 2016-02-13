package com.gmail.htaihm.nytimessearch.articleview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gmail.htaihm.nytimessearch.R;
import com.gmail.htaihm.nytimessearch.model.Article;
import com.gmail.htaihm.nytimessearch.model.ArticleMultimedia;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity {
    private static final String INTENT_EXTRA_ARTICLE = "com.gmail.htaihm.nytimessearch.article";

    @Bind(R.id.tvDetailHeadline) TextView mTvHeadline;
    @Bind(R.id.ivDetailImage) ImageView mIvDetailImage;
    @Bind(R.id.tvDetailGoUrl) TextView mTvGoUrl;
    @Bind(R.id.tvDetailPubDate) TextView mTvDetailPubDate;
    @Bind(R.id.tvDetailNewsDesk) TextView mTvDetailNewsDesk;
    @Bind(R.id.tvDetailAbstract) TextView mTvDetailAbstract;

    private ShareActionProvider mShareActionProvider;
    private Article mArticle;

    public static Intent newIntent(Context context, Article article) {
        Intent i = new Intent(context, ArticleDetailActivity.class);
        i.putExtra(INTENT_EXTRA_ARTICLE, article);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        mArticle = getIntent().getParcelableExtra(INTENT_EXTRA_ARTICLE);

        mTvHeadline.setText(mArticle.getHeadline());

        ArticleMultimedia largestImage = null;
        for (ArticleMultimedia multimedia : mArticle.getMultimedia()) {
            if (multimedia.getType().equalsIgnoreCase("image")) {
                if (largestImage == null || multimedia.getWidth() > largestImage.getWidth()) {
                    largestImage = multimedia;
                }
            }
        }
        if (largestImage != null) {
            Glide.with(this)
                    .load(mArticle.getMultimediaUrl(largestImage))
                    .into(mIvDetailImage);
        }
        mIvDetailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to display the article
                Intent i = ArticleWebViewActivity.newIntent(ArticleDetailActivity.this, mArticle);
                // launch the activity
                startActivity(i);
            }
        });

        mTvGoUrl.setText(Html.fromHtml("<u>" + Html.escapeHtml(mArticle.getWebUrl()) + "</u>"));
        mTvGoUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to display the article
                Intent i = ArticleWebViewActivity.newIntent(ArticleDetailActivity.this, mArticle);
                // launch the activity
                startActivity(i);
            }
        });

        if (mArticle.getPubDate() != null) {
            mTvDetailPubDate.setText(mArticle.getPubDate().toString());
        }
        if (mArticle.getNewsDesk() != null) {
            mTvDetailNewsDesk.setText(mArticle.getNewsDesk());
        }

        if (mArticle.getAbstract() != null) {
            mTvDetailAbstract.setText(mArticle.getAbstract());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_article_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(createShareIntent());

        return true;
    }

    public Intent createShareIntent() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, mArticle.getHeadline());
        i.putExtra(Intent.EXTRA_TEXT, mArticle.getWebUrl());
        i.setType("text/plain");
        return i;
    }
}
