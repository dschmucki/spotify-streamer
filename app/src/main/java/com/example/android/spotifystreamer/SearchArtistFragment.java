package com.example.android.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SearchArtistFragment extends Fragment {

    private static final String LOG_TAG = SearchArtistFragment.class.getSimpleName();
    private static final String STATE_ARTISTS = "artists";
    private static final String STATE_SEARCH_TEXT = "searchText";

    private SearchArtistResultAdapter<SpotifyStreamerArtist> searchArtistResultAdapter;
    private EditText searchArtistEditText;
    private ListView searchArtistResultListView;
    private SpotifyService spotifyService;
    private ArrayList<SpotifyStreamerArtist> spotifyStreamerArtists;
    private String lastSearchString = "";

    private int position = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public interface Callback {
        public void onItemSelected(SpotifyStreamerArtist spotifyStreamerArtist);
    }

    public SearchArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotifyApi wrapper = new SpotifyApi();
        spotifyService = wrapper.getService();

        lastSearchString = "";
        spotifyStreamerArtists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        searchArtistResultAdapter = new SearchArtistResultAdapter<>(getActivity(), R.layout.list_item_search_artist);

        View rootView = inflater.inflate(R.layout.fragment_search_artist, container, false);
        searchArtistResultListView = (ListView) rootView.findViewById(R.id.search_artist_result_list_view);
        searchArtistResultListView.setAdapter(searchArtistResultAdapter);
        searchArtistResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                ((Callback) getActivity()).onItemSelected(searchArtistResultAdapter.getItem(pos));
                position = pos;
            }
        });
        searchArtistResultListView.setEmptyView(rootView.findViewById(R.id.empty_view));

        searchArtistEditText = (EditText) rootView.findViewById(R.id.search_artist_edit_text);
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
                        if (!lastSearchString.equalsIgnoreCase(s.toString())) {
                            lastSearchString = s.toString();
                            new SearchArtistTask().execute(lastSearchString);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        if (savedInstanceState != null) {
            spotifyStreamerArtists = savedInstanceState.getParcelableArrayList(STATE_ARTISTS);
            lastSearchString = savedInstanceState.getString(STATE_SEARCH_TEXT);
            showResults();
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            position = savedInstanceState.getInt(SELECTED_KEY);
            searchArtistResultListView.smoothScrollToPosition(position);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (spotifyStreamerArtists != null) {
            outState.putParcelableArrayList(STATE_ARTISTS, spotifyStreamerArtists);
        }
        if (!lastSearchString.isEmpty()) {
            outState.putString(STATE_SEARCH_TEXT, lastSearchString);
        }
        if (position != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, position);
        }
        super.onSaveInstanceState(outState);
    }

    private void showResults() {
        // clear old results first, then check if new results are available and display them
        searchArtistResultAdapter.clear();
        if (spotifyStreamerArtists.isEmpty() && !lastSearchString.isEmpty()) {
            Toast.makeText(getActivity(), R.string.no_results_found_toast, Toast.LENGTH_SHORT).show();
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
