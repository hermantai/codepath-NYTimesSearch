package com.gmail.htaihm.nytimessearch.articlesearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

    @Bind(R.id.rvResults) RecyclerView mRvResults;
    @Bind(R.id.pbSearch) ProgressBar mPbSearch;

    private ArrayList<Article> mArticles;
    private RecyclerView.Adapter<ArticleViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private BroadcastReceiver mNetworkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Network changed: " + intent);
            if (mEndlessRecyclerViewScrollListener != null) {
                mEndlessRecyclerViewScrollListener.enableLoadMore(
                        NetworkUtil.isNetworkAvailable(SearchActivity.this));
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mNetworkChangeReceiver);
    }

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
        mAdapter = new ArticlesAdapter();
        mRvResults.setAdapter(mAdapter);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL);
        mRvResults.setLayoutManager(manager);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                fetchArticles(QueryPreferences.getQuery(SearchActivity.this), page);
            }
        };

        mRvResults.addOnScrollListener(mEndlessRecyclerViewScrollListener);
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
                fetchArticles(query, 0);
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

    private void fetchArticles(String query, final int pageNumber) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "cb6d7de957f77c1d1804cd599b8ccdb7:14:74323340");
        params.put("page", pageNumber);
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
                    List<Article> newArticles = Article.fromJsonArray(articleJsonResults);
                    Log.d(TAG, "Loaded " + newArticles.size() + " items for page " + pageNumber);
                    if (newArticles.size() == 0) {
                        mEndlessRecyclerViewScrollListener.notifyNoMoreItems();
                    } else {
                        mEndlessRecyclerViewScrollListener.enableLoadMore(true);
                    }
                    if (pageNumber == 0) {
                        mArticles.clear();
                        mArticles.addAll(newArticles);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        int nextItemPosition = mArticles.size();
                        mArticles.addAll(newArticles);
                        mAdapter.notifyItemRangeInserted(nextItemPosition, newArticles.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setUiLoading(false);
            }

            @Override
            public void onFailure(
                    int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mEndlessRecyclerViewScrollListener.notifyLoadMoreFailed();

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
        } else {
            mPbSearch.setVisibility(View.GONE);
        }
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.ivImage) ImageView mIvImage;
        @Bind(R.id.tvTitle) TextView mTvTitle;

        Article mArticle;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindItem(Article article) {
            // clear not recycled image from convertView from last time
            mArticle = article;

            mIvImage.setImageResource(0);
            mTvTitle.setText(article.getHeadline());

            // populate the thumbnail image
            // remote download the image in the background

            String thumbnail = article.getThumbnail();

            if (!TextUtils.isEmpty(thumbnail)) {
                Glide.with(SearchActivity.this)
                        .load(thumbnail)
                        .into(mIvImage);
            }
        }

        @Override
        public void onClick(View v) {
            // create an intent to display the article
            Intent i = ArticleActivity.newIntent(SearchActivity.this, mArticle);
            // launch the activity
            startActivity(i);
        }
    }

    class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_article_result, parent, false);
            return new ArticleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ArticleViewHolder holder, int position) {
            holder.bindItem(mArticles.get(position));
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }
    }
}
