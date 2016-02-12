package com.gmail.htaihm.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Article implements Parcelable {
    private final static String TAG = "Article";

    private String mWebUrl;
    private String mThumbnail;
    private String mHeadlingString;
    private ArticleHeadline mHeadline;
    private List<ArticleMultimedia> mMultimedia = new ArrayList<>();

    /**
     * Should be called right after an Article is created.
     */
    private void init() {
        if (mHeadline != null) {
            mHeadlingString = mHeadline.getMain();
        }

        for (ArticleMultimedia multimedia : mMultimedia) {
            if (multimedia.getSubtype().equals("thumbnail")) {
                mThumbnail = "http://www.nytimes.com/" + multimedia.getUrl();
            }
        }
    }

    public static Article createArticle(JSONObject jsonObject) {
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(new AndroidFieldNamingStrategy())
                .create();
        Article article = gson.fromJson(jsonObject.toString(), Article.class);
        article.init();
        return article;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getHeadline() {
        return mHeadlingString;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(createArticle(array.getJSONObject(x)));
            } catch (JSONException je) {
                Log.e(TAG, "Error parsing articles", je);
            }
        }

        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mWebUrl);
        dest.writeString(this.mHeadlingString);
        dest.writeString(this.mThumbnail);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.mWebUrl = in.readString();
        this.mHeadlingString = in.readString();
        this.mThumbnail = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
