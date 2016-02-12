package com.gmail.htaihm.nytimessearch.model;

public class ArticleMultimedia {
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
}
