package com.example.android.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class TopTracksFragment extends Fragment {

    public static final String COUNTRY_CODE = "CH";

    private static final String LOG_TAG = TopTracksFragment.class.getSimpleName();
    private static final String STATE_TRACKS = "tracks";
    private static final String STATE_ARTIST_NAME = "artist";
    private static final String STATE_SPOTIFY_ID = "spotifyId";

    private Map<String, Object> queryMap;
    private TopTracksResultAdapter<Track> topTracksResultAdapter;
    private ListView topTracksResultListView;
    private SpotifyService spotifyService;
    private ArrayList<SpotifyStreamerTrack> spotifyStreamerTracks;
    private String artistName = "";
    private String artistNameToSearchFor = "";
    private String spotifyId = "";
    private String spotifyIdToSearchFor = "";

    public interface PlayerCallback {
        public void onItemSelected(String artistName, ArrayList<SpotifyStreamerTrack> spotifyStreamerTracks, int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queryMap = new HashMap<>();
        queryMap.put("country", COUNTRY_CODE);

        SpotifyApi wrapper = new SpotifyApi();
        spotifyService = wrapper.getService();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            String name = arguments.getString(SpotifyStreamerArtist.ARTIST_NAME);
            if (name != null) {
                artistNameToSearchFor = name;
            }
            String id = arguments.getString(SpotifyStreamerArtist.SPOTIFY_ID);
            if (id != null) {
                spotifyIdToSearchFor = id;
            }
        }

        topTracksResultAdapter = new TopTracksResultAdapter<>(getActivity(), R.layout.list_item_top_tracks);

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        topTracksResultListView = (ListView) rootView.findViewById(R.id.top_tracks_list_view);
        topTracksResultListView.setAdapter(topTracksResultAdapter);
        topTracksResultListView.setEmptyView(rootView.findViewById(R.id.empty_view));
        topTracksResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                               ((PlayerCallback) getActivity()).onItemSelected(artistName, spotifyStreamerTracks, position);
                                                           }
                                                       }
        );

        if (savedInstanceState != null) {
            artistName = savedInstanceState.getString(STATE_ARTIST_NAME);
            spotifyId = savedInstanceState.getString(STATE_SPOTIFY_ID);
            spotifyStreamerTracks = savedInstanceState.getParcelableArrayList(STATE_TRACKS);
            showResults();
        }

        if (!artistName.equals(artistNameToSearchFor) && !spotifyId.equals(spotifyIdToSearchFor)) {
            artistName = artistNameToSearchFor;
            spotifyId = spotifyIdToSearchFor;
            new SearchTopTracksTask().execute(spotifyIdToSearchFor);
        }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_TRACKS, spotifyStreamerTracks);
        outState.putString(STATE_ARTIST_NAME, artistNameToSearchFor);
        outState.putString(STATE_SPOTIFY_ID, spotifyIdToSearchFor);

        super.onSaveInstanceState(outState);
    }

    private void showResults() {
        topTracksResultAdapter.clear();
        if (spotifyStreamerTracks != null) {
            if (spotifyStreamerTracks.isEmpty()) {
                Toast.makeText(getActivity(), R.string.top_tracks_no_results, Toast.LENGTH_SHORT).show();
            } else {
                topTracksResultAdapter.addAll(spotifyStreamerTracks);
            }
        }
    }

    private class SearchTopTracksTask extends AsyncTask<String, Void, ArrayList<SpotifyStreamerTrack>> {

        @Override
        protected ArrayList<SpotifyStreamerTrack> doInBackground(String... params) {
            ArrayList<SpotifyStreamerTrack> trackList = new ArrayList<>();
            String searchString = params[0];
            if (searchString != null && !searchString.isEmpty()) {
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
