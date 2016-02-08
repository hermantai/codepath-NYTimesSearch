package com.gmail.htaihm.nytimessearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Article implements Serializable {
    private final static String TAG = "Article";

    String mWebUrl;
    String mHeadline;
    String mThumbnail;

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
}
