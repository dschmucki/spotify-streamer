package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;
import com.example.android.spotifystreamer.data.SpotifyStreamerTrack;

import java.util.ArrayList;

/**
 * Created by domi on 04.08.15.
 */
public class TopTracksActivity extends AppCompatActivity implements TopTracksFragment.PlayerCallback {

    public static final String TOP_TRACKS_FRAGMENT_TAG = "TTFT";

    Fragment topTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        topTracksFragment = getSupportFragmentManager().findFragmentByTag(TOP_TRACKS_FRAGMENT_TAG);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString(SpotifyStreamerArtist.SPOTIFY_ID, getIntent().getStringExtra(SpotifyStreamerArtist.SPOTIFY_ID));
            arguments.putString(SpotifyStreamerArtist.ARTIST_NAME, getIntent().getStringExtra(SpotifyStreamerArtist.ARTIST_NAME));

            topTracksFragment = new TopTracksFragment();
            topTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, topTracksFragment, TOP_TRACKS_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public void onItemSelected(String artistName, ArrayList<SpotifyStreamerTrack> spotifyStreamerTracks, int position) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(SpotifyStreamerArtist.ARTIST_NAME, artistName);
        intent.putParcelableArrayListExtra(SpotifyStreamerTrack.TRACK_PARCELABLE, spotifyStreamerTracks);
        intent.putExtra(SpotifyStreamerTrack.POSITION, position);
        startActivity(intent);
    }
}
