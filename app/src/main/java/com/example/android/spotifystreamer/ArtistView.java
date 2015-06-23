package com.example.android.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by domi on 21.06.15.
 */
public interface ArtistView {

    /**
     * Shows an empty view, no results.
     */
    public void showEmpty();

    /**
     * Shows a message that no results were found.
     */
    public void showNoSearchResults();

    /**
     * Shows loading progress.
     */
    public void showLoading();

    /**
     * Hide loading progess.
     */
    public void hideLoading();

    /**
     * Shows results in a view.
     *
     * @param artists list of artists to display
     */
    public void showSearchResults(List<Artist> artists);

    /**
     * Show search text.
     *
     * @param text search text user entered
     */
    public void showSearchText(String text);
}
