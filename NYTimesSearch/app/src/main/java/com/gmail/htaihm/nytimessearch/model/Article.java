package com.gmail.htaihm.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article implements Parcelable {
    private final static String TAG = "Article";

    private String mWebUrl;
    private String mHeadline;
    private String mThumbnail;

    public Article(JSONObject jsonObject) {
        try {
            this.mWebUrl = jsonObject.getString("web_url");
            this.mHeadline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                mThumbnail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                mThumbnail = "";
            }
        } catch (JSONException je) {
            Log.e(TAG, "Error parsing an Article", je);
        }
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new Article(array.getJSONObject(x)));
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
        dest.writeString(this.mHeadline);
        dest.writeString(this.mThumbnail);
    }

    protected Article(Parcel in) {
        this.mWebUrl = in.readString();
        this.mHeadline = in.readString();
        this.mThumbnail = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
