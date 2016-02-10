package com.gmail.htaihm.nytimessearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.gmail.htaihm.nytimessearch.model.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    @Bind(R.id.etQuery) EditText mEtQuery;
    @Bind(R.id.gvResults) GridView mGvResults;
    @Bind(R.id.btnSearch) Button mBtnSearch;

    private ArrayList<Article> mArticles;

    ArticleArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        setUpViews();
    }

    private void setUpViews() {
        ButterKnife.bind(this);

        mArticles = new ArrayList<>();
        mAdapter = new ArticleArrayAdapter(this, mArticles);
        mGvResults.setAdapter(mAdapter);

        mGvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Article article = mAdapter.getItem(position);
                Intent i = ArticleActivity.newIntent(SearchActivity.this, article);

                // launch the activity
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_filter) {
            SearchFilterFragment frag = SearchFilterFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            frag.show(fm, "SearchFilterFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query = mEtQuery.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "cb6d7de957f77c1d1804cd599b8ccdb7:14:74323340");
        params.put("page", 0);
        params.put("q", query);

        if (BuildConfig.DEBUG) {
            Log.d(
                    TAG,
                    String.format(
                            "Search article with url: %s, request params: %s",
                            url,
                            params));
        }

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());

                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d(TAG, articleJsonResults.toString());
                    mAdapter.addAll(Article.fromJsonArray(articleJsonResults));
                    Log.d(TAG, mArticles.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(
                    int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ErrorHandling.handleError(
                        SearchActivity.this,
                        TAG,
                        String.format(
                                "Status; %d, error: %s, msg: %s",
                                statusCode,
                                throwable,
                                errorResponse),
                        throwable);
            }
        });
    }
}
