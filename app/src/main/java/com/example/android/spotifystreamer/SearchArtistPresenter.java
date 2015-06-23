package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by domi on 17.06.15.
 */
public class SearchArtistPresenter implements Presenter {

    private static final String LOG_TAG = SearchArtistPresenter.class.getSimpleName();

    public static final String ARTIST_ID = "ARTIST_ID";
    public static final String ARTIST_NAME = "ARTIST_NAME";

    private static SearchArtistPresenter instance = null;

    private SpotifyService spotifyService;
    private ArtistView artistView;

    private List<Artist> artistSearchResults;
    private String lastSearchString;

    private SearchArtistTask searchArtistTask;

    protected SearchArtistPresenter() {
        SpotifyApi wrapper = new SpotifyApi();
        spotifyService = wrapper.getService();
    }

    public static SearchArtistPresenter getInstance() {
        if (instance == null) {
            instance = new SearchArtistPresenter();
            Log.d(LOG_TAG, SearchArtistPresenter.class.getSimpleName() + " created");
        }
        return instance;
    }

    public void setArtistView(final SearchArtistActivity artistView) {
        this.artistView = artistView;
    }

    public void searchArtist(final String searchString) {
        if (!searchString.equals(lastSearchString)) {
            lastSearchString = searchString;
            if (searchArtistTask != null && AsyncTask.Status.RUNNING == searchArtistTask.getStatus()) {
                searchArtistTask.cancel(true);
            }
            searchArtistTask = new SearchArtistTask();
            searchArtistTask.execute(searchString);
        }
    }

    public void showTopTracks(final Context context, final Artist artist) {
        Intent intent = new Intent(context, TopTracksActivity.class);
        intent.putExtra(ARTIST_ID, artist.id);
        intent.putExtra(ARTIST_NAME, artist.name);
        context.startActivity(intent);
    }

    private void setAndNotifyNewSearchResults(final List<Artist> artists) {
        artistSearchResults = artists;
        if (artistSearchResults.isEmpty()) {
            artistView.showEmpty();
            if (!lastSearchString.isEmpty()) {
                artistView.showNoSearchResults();
            }
        } else {
            artistView.showSearchResults(artistSearchResults);
        }
    }

    @Override
    public void initialize() {
        if (artistSearchResults == null) {
            artistSearchResults = new ArrayList<>();
        }
    }

    @Override
    public void resume() {
        artistView.showSearchResults(artistSearchResults);
        artistView.showSearchText(lastSearchString);
    }

    private class SearchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        @Override
        protected List<Artist> doInBackground(String... params) {
            List<Artist> artists = new ArrayList<>();
            String searchString = params[0];
            if (!searchString.isEmpty()) {
                try {
                    ArtistsPager artistsPager = spotifyService.searchArtists(searchString);
                    artists.addAll(artistsPager.artists.items);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            Log.d(LOG_TAG, "Number of search results for " + searchString + ": " + artists.size());
            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            setAndNotifyNewSearchResults(artists);
        }
    }
}
