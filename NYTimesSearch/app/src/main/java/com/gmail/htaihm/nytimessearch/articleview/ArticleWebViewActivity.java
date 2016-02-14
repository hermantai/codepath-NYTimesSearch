package com.gmail.htaihm.nytimessearch.articleview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gmail.htaihm.nytimessearch.R;
import com.gmail.htaihm.nytimessearch.model.Article;

public class ArticleWebViewActivity extends AppCompatActivity {
    private static final String INTENT_EXTRA_ARTICLE = "com.gmail.htaihm.nytimessearch.article";

    private ShareActionProvider mShareActionProvider;
    private Article mArticle;

    public static Intent newIntent(Context context, Article article) {
        Intent i = new Intent(context, ArticleWebViewActivity.class);
        i.putExtra(INTENT_EXTRA_ARTICLE, article);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ProgressBar pbLoadingProgressBar = (ProgressBar) findViewById(
                R.id.pbLoadingProgressBar);
        pbLoadingProgressBar.setMax(100);  // WebChromeClient reports in range 0-100

        mArticle = getIntent().getParcelableExtra(INTENT_EXTRA_ARTICLE);
        String url = mArticle.getWebUrl();

        WebView webView = (WebView) findViewById(R.id.wvArticle);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pbLoadingProgressBar.setVisibility(View.GONE);
                } else {
                    pbLoadingProgressBar.setVisibility(View.VISIBLE);
                    pbLoadingProgressBar.setProgress(newProgress);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_article_web_view, menu);
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
