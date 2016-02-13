package com.gmail.htaihm.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Article implements Parcelable {
    private final static String TAG = "Article";

    private String mWebUrl;
    private String mThumbnail;
    private String mHeadlingString;
    private ArticleHeadline mHeadline;
    private List<ArticleMultimedia> mMultimedia = new ArrayList<>();
    private Date mPubDate;
    private String mNewsDesk;

    /**
     * Should be called right after an Article is created.
     */
    private void init() {
        if (mHeadline != null) {
            mHeadlingString = StringEscapeUtils.unescapeHtml4(mHeadline.getMain());
        }

        for (ArticleMultimedia multimedia : mMultimedia) {
            if (multimedia.getSubtype().equals("thumbnail")) {
                mThumbnail = getMultimediaUrl(multimedia);
            }
        }
    }

    public String getMultimediaUrl(ArticleMultimedia multimedia) {
        return "http://www.nytimes.com/" + multimedia.getUrl();
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
                Article article = createArticle(array.getJSONObject(x));
                Log.d(TAG, "Article created: " + article);
                results.add(article);
            } catch (JSONException je) {
                Log.e(TAG, "Error parsing articles", je);
            }
        }

        return results;
    }

    public List<ArticleMultimedia> getMultimedia() {
        return mMultimedia;
    }

    public Date getPubDate() {
        return mPubDate;
    }

    public String getNewsDesk() {
        return mNewsDesk;
    }

    @Override
    public String toString() {
        return "Article{" +
                "mWebUrl='" + mWebUrl + '\'' +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mHeadlingString='" + mHeadlingString + '\'' +
                ", mHeadline=" + mHeadline +
                ", mMultimedia=" + mMultimedia +
                ", mPubDate=" + mPubDate +
                ", mNewsDesk='" + mNewsDesk + '\'' +
                '}';
    }

    public Article() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mWebUrl);
        dest.writeString(this.mThumbnail);
        dest.writeString(this.mHeadlingString);
        dest.writeTypedList(mMultimedia);
        dest.writeLong(mPubDate != null ? mPubDate.getTime() : -1);
        dest.writeString(this.mNewsDesk);
    }

    protected Article(Parcel in) {
        this.mWebUrl = in.readString();
        this.mThumbnail = in.readString();
        this.mHeadlingString = in.readString();
        this.mMultimedia = in.createTypedArrayList(ArticleMultimedia.CREATOR);
        long tmpMPubDate = in.readLong();
        this.mPubDate = tmpMPubDate == -1 ? null : new Date(tmpMPubDate);
        this.mNewsDesk = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
