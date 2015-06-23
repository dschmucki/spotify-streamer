package com.example.android.spotifystreamer;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by domi on 17.06.15.
 */
public class TopTracksPresenter implements Presenter {

    public static final String COUNTRY_CODE = "CH";

    private final static String LOG_TAG = TopTracksPresenter.class.getSimpleName();

    private static TopTracksPresenter instance = null;

    private SpotifyService spotifyService;
    private TopTracksView topTracksView;

    private List<Track> topTrackSearchResults;
    private String lastSearchString;
    private Map<String, Object> queryMap;

    private SearchTopTracksTask searchTopTracksTask;

    protected TopTracksPresenter() {
        SpotifyApi wrapper = new SpotifyApi();
        spotifyService = wrapper.getService();
        queryMap = new HashMap<>();
        queryMap.put("country", COUNTRY_CODE);
    }

    public static TopTracksPresenter getInstance() {
        if (instance == null) {
            instance = new TopTracksPresenter();
            Log.d(LOG_TAG, TopTracksPresenter.class.getSimpleName() + " created");
        }
        return instance;
    }

    public void setTopTracksView(final TopTracksView topTracksView) {
        this.topTracksView = topTracksView;
    }

    public void searchTopTracks(final String searchString) {
        if (!searchString.equals(lastSearchString)) {
            lastSearchString = searchString;
            if (searchTopTracksTask != null && AsyncTask.Status.RUNNING == searchTopTracksTask.getStatus()) {
                searchTopTracksTask.cancel(true);
            }
            searchTopTracksTask = new SearchTopTracksTask();
            searchTopTracksTask.execute(searchString);
        }
    }

    public void backPressed() {
        topTrackSearchResults.clear();
    }

    private void setAndNotifyNewSearchResults(final List<Track> tracks) {
        topTrackSearchResults = tracks;
        if (topTrackSearchResults.isEmpty()) {
            topTracksView.showEmpty();
        } else {
            topTracksView.showSearchResults(topTrackSearchResults);
        }
    }

    @Override
    public void initialize() {
        if (topTrackSearchResults == null) {
            topTrackSearchResults = new ArrayList<>();
        }
    }

    @Override
    public void resume() {
        topTracksView.showSearchResults(topTrackSearchResults);
    }

    private class SearchTopTracksTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            List<Track> tracksList = new ArrayList<>();
            String searchString = params[0];
            if (!searchString.isEmpty()) {
                try {
                    Tracks tracks = spotifyService.getArtistTopTrack(searchString, queryMap);
                    tracksList.addAll(tracks.tracks);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            Log.d(LOG_TAG, "Number of search results for " + searchString + ": " + tracksList.size());
            return tracksList;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            setAndNotifyNewSearchResults(tracks);
        }
    }
}
