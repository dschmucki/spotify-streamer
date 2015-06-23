package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;
import com.example.android.spotifystreamer.data.SpotifyStreamerTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Displays the top tracks for a given artist.
 *
 * @author dschmucki
 */
public class TopTracksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String COUNTRY_CODE = "CH";

    private static final String LOG_TAG = TopTracksActivity.class.getSimpleName();
    private static final String STATE_TRACKS = "tracks";
    private static final String STATE_ARTIST_NAME = "artist";

    private Map<String, Object> queryMap;
    private TopTracksResultAdapter<Track> topTracksResultAdapter;
    private ListView topTracksResultListView;
    private SpotifyService spotifyService;
    private ArrayList<SpotifyStreamerTrack> spotifyStreamerTracks;
    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_tracks_activity);

        SpotifyApi wrapper = new SpotifyApi();
        spotifyService = wrapper.getService();


        topTracksResultAdapter = new TopTracksResultAdapter<>(this, R.layout.top_tracks_result);

        topTracksResultListView = (ListView) findViewById(R.id.top_tracks_list_view);
        topTracksResultListView.setAdapter(topTracksResultAdapter);
        topTracksResultListView.setEmptyView(findViewById(R.id.empty_view));
        topTracksResultListView.setOnItemClickListener(this);

        queryMap = new HashMap<>();
        queryMap.put("country", COUNTRY_CODE);

        // recreate state or check Intent
        if (savedInstanceState != null) {
            spotifyStreamerTracks = savedInstanceState.getParcelableArrayList(STATE_TRACKS);
            artistName = savedInstanceState.getString(STATE_ARTIST_NAME);
            showResults();
        } else {
            Intent intent = getIntent();
            new SearchTopTracksTask().execute(intent.getStringExtra(SpotifyStreamerArtist.SPOTIFY_ID));
            artistName = intent.getStringExtra(SpotifyStreamerArtist.ARTIST_NAME);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(artistName);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Stage 2
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        topTracksResultAdapter.clear();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_TRACKS, spotifyStreamerTracks);
        outState.putString(STATE_ARTIST_NAME, artistName);
        super.onSaveInstanceState(outState);
    }

    private void showResults() {
        topTracksResultAdapter.clear();
        if (spotifyStreamerTracks.isEmpty()) {
            Toast.makeText(TopTracksActivity.this, R.string.top_tracks_no_results, Toast.LENGTH_SHORT).show();
        } else {
            topTracksResultAdapter.addAll(spotifyStreamerTracks);
        }
    }

    private class SearchTopTracksTask extends AsyncTask<String, Void, ArrayList<SpotifyStreamerTrack>> {

        @Override
        protected ArrayList<SpotifyStreamerTrack> doInBackground(String... params) {
            ArrayList<SpotifyStreamerTrack> trackList = new ArrayList<>();
            String searchString = params[0];
            if (!searchString.isEmpty()) {
                try {
                    Tracks tracks = spotifyService.getArtistTopTrack(searchString, queryMap);
                    trackList.addAll(parcelTracks(tracks));
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            Log.d(LOG_TAG, "Number of search results for " + searchString + ": " + trackList.size());
            return trackList;
        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyStreamerTrack> tracks) {
            spotifyStreamerTracks = tracks;
            showResults();
        }

        private ArrayList<SpotifyStreamerTrack> parcelTracks(Tracks tracks) {
            ArrayList<SpotifyStreamerTrack> trackList = new ArrayList<>();
            for (Track track : tracks.tracks) {
                SpotifyStreamerTrack spotifyStreamerTrack = new SpotifyStreamerTrack(track.name, track.album.name, getSmallImageUrl(track.album.images), getLargeImageUrl(track.album.images), track.preview_url);
                trackList.add(spotifyStreamerTrack);
            }
            return trackList;
        }

        private String getSmallImageUrl(List<Image> images) {
            for (Image i : images) {
                if (i.height == R.dimen.small_image_size || i.width == R.dimen.small_image_size) {
                    return i.url;
                }
            }
            return images.size() > 0 ? images.get(0).url : null;
        }

        private String getLargeImageUrl(List<Image> images) {
            for (Image i : images) {
                if (i.height >= R.dimen.large_image_size || i.width >= R.dimen.large_image_size) {
                    return i.url;
                }
            }
            return images.size() > 0 ? images.get(0).url : null;
        }
    }
}
