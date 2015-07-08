package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Shows an {@link EditText} for searching artists on Spotify. Displays a list of search results underneath.
 *
 * @author dschmucki
 */
public class SearchArtistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = SearchArtistActivity.class.getSimpleName();
    private static final String STATE_ARTISTS = "artists";
    private static final String STATE_SEARCH_TEXT = "searchText";

    private SearchArtistResultAdapter<SpotifyStreamerArtist> searchArtistResultAdapter;
    private EditText searchArtistEditText;
    private ListView searchArtistResultListView;
    private SpotifyService spotifyService;
    private ArrayList<SpotifyStreamerArtist> spotifyStreamerArtists;
    private String lastSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_artist_activity);
        searchArtistResultAdapter = new SearchArtistResultAdapter<>(this, R.layout.search_artist_result);

        SpotifyApi wrapper = new SpotifyApi();
        spotifyService = wrapper.getService();

        if (savedInstanceState != null) {
            spotifyStreamerArtists = savedInstanceState.getParcelableArrayList(STATE_ARTISTS);
            lastSearchString = savedInstanceState.getString(STATE_SEARCH_TEXT);
            showResults();
        }

        searchArtistResultListView = (ListView) findViewById(R.id.search_artist_result_list_view);
        searchArtistResultListView.setAdapter(searchArtistResultAdapter);
        searchArtistResultListView.setOnItemClickListener(this);
        searchArtistResultListView.setEmptyView(findViewById(R.id.empty_view));

        searchArtistEditText = (EditText) findViewById(R.id.search_artist_edit_text);
        searchArtistEditText.setText(lastSearchString);
        // avoid searching again during orientation change by adding listener after the activity has been created
        searchArtistEditText.post(new Runnable() {

            @Override
            public void run() {
                searchArtistEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        lastSearchString = s.toString();
                        new SearchArtistTask().execute(lastSearchString);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_ARTISTS, spotifyStreamerArtists);
        outState.putString(STATE_SEARCH_TEXT, lastSearchString);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // create an Intent to start TopTracksActivity with SpotifyId of selected artist
        SpotifyStreamerArtist spotifyStreamerArtist = searchArtistResultAdapter.getItem(position);
        Intent intent = new Intent(this, TopTracksActivity.class);
        intent.putExtra(SpotifyStreamerArtist.SPOTIFY_ID, spotifyStreamerArtist.getSpotifyId());
        intent.putExtra(SpotifyStreamerArtist.ARTIST_NAME, spotifyStreamerArtist.getArtistName());
        startActivity(intent);
    }

    private void showResults() {
        // clear old results first, then check if new results are available and display them
        searchArtistResultAdapter.clear();
        if (spotifyStreamerArtists.isEmpty() && !lastSearchString.isEmpty()) {
            Toast.makeText(SearchArtistActivity.this, R.string.no_results_found_toast, Toast.LENGTH_SHORT).show();
        } else {
            searchArtistResultAdapter.addAll(spotifyStreamerArtists);
        }
    }

    private class SearchArtistTask extends AsyncTask<String, Void, ArrayList<SpotifyStreamerArtist>> {

        @Override
        protected ArrayList<SpotifyStreamerArtist> doInBackground(String... params) {
            ArrayList<SpotifyStreamerArtist> artists = new ArrayList<>();
            String searchString = params[0];
            if (!searchString.isEmpty()) {
                try {
                    ArtistsPager artistsPager = spotifyService.searchArtists(searchString);
                    artists.addAll(parcelArtists(artistsPager));
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            Log.d(LOG_TAG, "Number of search results for " + searchString + ": " + artists.size());
            return artists;
        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyStreamerArtist> artists) {
            spotifyStreamerArtists = artists;
            showResults();
        }

        private ArrayList<SpotifyStreamerArtist> parcelArtists(ArtistsPager artistsPager) {
            ArrayList<SpotifyStreamerArtist> artists = new ArrayList<>();
            for (Artist artist : artistsPager.artists.items) {
                SpotifyStreamerArtist spotifyStreamerArtist = new SpotifyStreamerArtist(artist.name, artist.id, getSmallImageUrl(artist.images));
                artists.add(spotifyStreamerArtist);
            }
            return artists;
        }

        private String getSmallImageUrl(List<Image> images) {
            for (Image i : images) {
                if (i.height == R.dimen.small_image_size || i.width == R.dimen.small_image_size) {
                    return i.url;
                }
            }
            int size = images.size();
            // get smallest image, images are sorted by size, largest first
            return size > 0 ? images.get(size - 1).url : null;
        }
    }

}
