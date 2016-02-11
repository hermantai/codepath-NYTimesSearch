package com.gmail.htaihm.nytimessearch.articlesearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gmail.htaihm.nytimessearch.BuildConfig;
import com.gmail.htaihm.nytimessearch.R;
import com.gmail.htaihm.nytimessearch.articleview.ArticleActivity;
import com.gmail.htaihm.nytimessearch.helper.ErrorHandling;
import com.gmail.htaihm.nytimessearch.helper.LogUtil;
import com.gmail.htaihm.nytimessearch.helper.NetworkUtil;
import com.gmail.htaihm.nytimessearch.model.Article;
import com.gmail.htaihm.nytimessearch.repo.QueryPreferences;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final SimpleDateFormat dateFormatterForApi = new SimpleDateFormat("yyyyMMdd");

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
                QueryPreferences.setQuery(SearchActivity.this, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = QueryPreferences.getQuery(SearchActivity.this);
                if (!TextUtils.isEmpty(q)) {
                    searchView.setQuery(q, false);
                }
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
            case R.id.action_settings:
                SettingsFragment frag = SettingsFragment.newInstance();
                FragmentManager fm = getSupportFragmentManager();
                frag.show(fm, "SettingsFragment");
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

        long beginDateMillis = QueryPreferences.getFilterBeginDate(this);
        if (beginDateMillis != 0) {
            Date d = new Date(beginDateMillis);
            params.put("begin_date", dateFormatterForApi.format(d));
        }
        String sortOrder = QueryPreferences.getFilterSortOrder(this);
        if (sortOrder != null) {
            sortOrder = sortOrder.toLowerCase();
        }
        if (TextUtils.equals("oldest", sortOrder) || TextUtils.equals("newest", sortOrder)) {
            params.put("sort", sortOrder);
        }

        List<String> newsDeskValues = new ArrayList<>();
        if (QueryPreferences.isFilterNewsDeskValueArts(this)) {
            newsDeskValues.add("Arts");
        }
        if (QueryPreferences.isFilterNewsDeskValueFashionAndStyle(this)) {
            newsDeskValues.add("Fashion & Style");
        }
        if (QueryPreferences.isFilterNewsDeskValueSports(this)) {
            newsDeskValues.add("Sports");
        }

        if (!newsDeskValues.isEmpty()) {
            StringBuilder sb = new StringBuilder("news_desk:(\"");
            sb.append(newsDeskValues.get(0));
            sb.append('"');

            for (int i = 1; i < newsDeskValues.size(); i++) {
                sb.append(" \"");
                sb.append(newsDeskValues.get(i));
                sb.append('"');
            }
            sb.append(')');
            params.put("fq", sb.toString());
        }

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
                LogUtil.d(TAG, "Response: " + response.toString());

                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
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
