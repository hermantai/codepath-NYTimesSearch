package com.gmail.htaihm.nytimessearch.articleview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
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

        final Article article = getIntent().getParcelableExtra(INTENT_EXTRA_ARTICLE);

        mTvHeadline.setText(article.getHeadline());

        ArticleMultimedia largestImage = null;
        for (ArticleMultimedia multimedia : article.getMultimedia()) {
            Log.d("DEBUG", "multi: " + multimedia);
            if (multimedia.getType().equalsIgnoreCase("image")) {
                if (largestImage == null || multimedia.getWidth() > largestImage.getWidth()) {
                    Log.d("DEBUG", "i am in");
                    largestImage = multimedia;
                }
            }
        }
        if (largestImage != null) {
            Log.d("DEBUG", "load image: " + article.getMultimediaUrl(largestImage));
            Log.d("DEBUG", "image: " + largestImage);

            Glide.with(this)
                    .load(article.getMultimediaUrl(largestImage))
                    .into(mIvDetailImage);
        }
        mIvDetailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to display the article
                Intent i = ArticleWebViewActivity.newIntent(ArticleDetailActivity.this, article);
                // launch the activity
                startActivity(i);
            }
        });

        mTvGoUrl.setText(Html.fromHtml("<u>" + Html.escapeHtml(article.getWebUrl()) + "</u>"));
        mTvGoUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to display the article
                Intent i = ArticleWebViewActivity.newIntent(ArticleDetailActivity.this, article);
                // launch the activity
                startActivity(i);
            }
        });

        if (article.getPubDate() != null) {
            mTvDetailPubDate.setText(article.getPubDate().toString());
        }
        if (article.getNewsDesk() != null) {
            mTvDetailNewsDesk.setText(article.getNewsDesk());
        }

    }
}
