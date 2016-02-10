package com.gmail.htaihm.nytimessearch.repo;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_FILTER_BEGIN_DATE = "beginDate";
    private static final String PREF_FILTER_SORT_ORDER = "sortOrder";
    private static final String PREF_FILTER_NEWS_DESK_VALUE_ARTS = "arts";
    private static final String PREF_FILTER_NEWS_DESK_VALUE_FASHION_AND_STYLE = "fashion_and_style";
    private static final String PREF_FILTER_NEWS_DESK_VALUE_SPORTS = "sports";

    public static String getQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static long getFilterBeginDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PREF_FILTER_BEGIN_DATE, 0);
    }

    public static void setFilterBeginDate(Context context, long timeInMillis) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PREF_FILTER_BEGIN_DATE, timeInMillis)
                .apply();
    }

    public static String getFilterSortOrder(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_FILTER_SORT_ORDER, null);
    }

    public static void setFilterSortOrder(Context context, String sortOrder) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_FILTER_SORT_ORDER, sortOrder)
                .apply();
    }

    public static boolean isFilterNewsDeskValueArts(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_FILTER_NEWS_DESK_VALUE_ARTS, false);
    }

    public static void setIsFilterNewsDeskValueArts(Context context, boolean isNewsDeskValueArts) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_FILTER_NEWS_DESK_VALUE_ARTS, isNewsDeskValueArts)
                .apply();
    }

    public static boolean isFilterNewsDeskValueFashionAndStyle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_FILTER_NEWS_DESK_VALUE_FASHION_AND_STYLE, false);
    }

    public static void setIsFilterNewsDeskValueFashionAndStyle(
            Context context, boolean isNewsDeskValueFashionAndStyle) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(
                        PREF_FILTER_NEWS_DESK_VALUE_FASHION_AND_STYLE,
                        isNewsDeskValueFashionAndStyle)
                .apply();
    }

    public static boolean isFilterNewsDeskValueSports(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_FILTER_NEWS_DESK_VALUE_SPORTS, false);
    }

    public static void setIsFilterNewsDeskValueSports(
            Context context, boolean isNewsDeskValueSports) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_FILTER_NEWS_DESK_VALUE_SPORTS, isNewsDeskValueSports)
                .apply();
    }
}