package com.gmail.htaihm.nytimessearch.providers;

import android.content.SearchRecentSuggestionsProvider;

public class ArticleSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.gmail.htaihm.nytimessearch.providers" +
            ".ArticleSearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ArticleSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
