package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;
import com.example.android.spotifystreamer.data.SpotifyStreamerTrack;

import java.util.ArrayList;

/**
 * Created by domi on 04.08.15.
 */
public class SearchArtistActivity extends AppCompatActivity implements SearchArtistFragment.Callback, TopTracksFragment.PlayerCallback {

    private boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);

        if (findViewById(R.id.top_tracks_container) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.top_tracks_container, new TopTracksFragment(), TopTracksActivity.TOP_TRACKS_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            twoPane = false;
        }
    }

    @Override
    public void onItemSelected(SpotifyStreamerArtist spotifyStreamerArtist) {
        if (twoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(SpotifyStreamerArtist.SPOTIFY_ID, spotifyStreamerArtist.getSpotifyId());
            arguments.putString(SpotifyStreamerArtist.ARTIST_NAME, spotifyStreamerArtist.getArtistName());

            TopTracksFragment topTracksFragment = new TopTracksFragment();
            topTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, topTracksFragment, TopTracksActivity.TOP_TRACKS_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra(SpotifyStreamerArtist.SPOTIFY_ID, spotifyStreamerArtist.getSpotifyId());
            intent.putExtra(SpotifyStreamerArtist.ARTIST_NAME, spotifyStreamerArtist.getArtistName());
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(String artistName, ArrayList<SpotifyStreamerTrack> spotifyStreamerTracks, int position) {
        Bundle arguments = new Bundle();
        arguments.putString(SpotifyStreamerArtist.ARTIST_NAME, artistName);
        arguments.putParcelableArrayList(SpotifyStreamerTrack.TRACK_PARCELABLE, spotifyStreamerTracks);
        arguments.putInt(SpotifyStreamerTrack.POSITION, position);

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(arguments);

        playerFragment.show(getSupportFragmentManager(), "dialog");
    }
}
