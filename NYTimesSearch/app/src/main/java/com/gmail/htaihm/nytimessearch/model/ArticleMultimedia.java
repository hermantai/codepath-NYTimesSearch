package com.gmail.htaihm.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticleMultimedia implements Parcelable {
    private String mUrl;
    private String mType;
    private String mSubtype;
    private int width;
    private int height;

    public ArticleMultimedia() {
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {

        mUrl = url;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getSubtype() {
        return mSubtype;
    }

    public void setSubtype(String subtype) {
        mSubtype = subtype;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ArticleMultimedia{" +
                "mUrl='" + mUrl + '\'' +
                ", mType='" + mType + '\'' +
                ", mSubtype='" + mSubtype + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUrl);
        dest.writeString(this.mType);
        dest.writeString(this.mSubtype);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected ArticleMultimedia(Parcel in) {
        this.mUrl = in.readString();
        this.mType = in.readString();
        this.mSubtype = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<ArticleMultimedia> CREATOR = new Parcelable
            .Creator<ArticleMultimedia>() {
        public ArticleMultimedia createFromParcel(Parcel source) {
            return new ArticleMultimedia(source);
        }

        public ArticleMultimedia[] newArray(int size) {
            return new ArticleMultimedia[size];
        }
    };
}
