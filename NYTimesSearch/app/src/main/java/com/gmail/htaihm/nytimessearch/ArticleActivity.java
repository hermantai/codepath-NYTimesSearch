package com.gmail.htaihm.nytimessearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.Serializable;

public class ArticleActivity extends AppCompatActivity {
    private static final String INTENT_EXTRA_ARTICLE = "com.gmail.htaihm.nytimessearch.article";

    public static Intent newIntent(Context context, Article article) {
        Intent i = new Intent(context, ArticleActivity.class);
        i.putExtra(INTENT_EXTRA_ARTICLE, (Serializable) article);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Article article = (Article) getIntent().getSerializableExtra(INTENT_EXTRA_ARTICLE);
        String url = article.getWebUrl();

        WebView webView = (WebView) findViewById(R.id.wvArticle);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(url);
    }

}
