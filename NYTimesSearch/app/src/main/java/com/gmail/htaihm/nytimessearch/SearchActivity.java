package com.gmail.htaihm.nytimessearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    @Bind(R.id.gvResults) GridView mGvResults;
    @Bind(R.id.pbSearch) ProgressBar mPbSearch;

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

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchArticles(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search_filter:
                SearchFilterFragment frag = SearchFilterFragment.newInstance();
                FragmentManager fm = getSupportFragmentManager();
                frag.show(fm, "SearchFilterFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void fetchArticles(String query) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
            return;
        }

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

        setUiLoading(true);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());

                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d(TAG, "Json response: " + articleJsonResults.toString());
                    mAdapter.addAll(Article.fromJsonArray(articleJsonResults));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setUiLoading(false);
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
                setUiLoading(false);
            }
        });
    }

    private void setUiLoading(boolean isLoading) {
        if (isLoading) {
            mPbSearch.setVisibility(View.VISIBLE);
            mGvResults.setVisibility(View.GONE);
        } else {
            mPbSearch.setVisibility(View.GONE);
            mGvResults.setVisibility(View.VISIBLE);
        }
    }
}
