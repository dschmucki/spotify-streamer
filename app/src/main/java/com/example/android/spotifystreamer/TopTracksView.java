package com.example.android.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by domi on 21.06.15.
 */
public interface TopTracksView {

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
     * Show results in view.
     */
    public void showSearchResults(List<Track> tracks);
}
