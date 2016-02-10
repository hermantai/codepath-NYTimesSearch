package com.gmail.htaihm.nytimessearch.articleview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gmail.htaihm.nytimessearch.R;
import com.gmail.htaihm.nytimessearch.model.Article;

public class ArticleActivity extends AppCompatActivity {
    private static final String INTENT_EXTRA_ARTICLE = "com.gmail.htaihm.nytimessearch.article";

    public static Intent newIntent(Context context, Article article) {
        Intent i = new Intent(context, ArticleActivity.class);
        i.putExtra(INTENT_EXTRA_ARTICLE, article);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        final ProgressBar pbLoadingProgressBar = (ProgressBar) findViewById(R.id.pbLoadingProgressBar);
        pbLoadingProgressBar.setMax(100);  // WebChromeClient reports in range 0-100

        Article article = getIntent().getParcelableExtra(INTENT_EXTRA_ARTICLE);
        String url = article.getWebUrl();

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

}
