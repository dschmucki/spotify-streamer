package com.example.android.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;
import com.example.android.spotifystreamer.data.SpotifyStreamerTrack;

/**
 * Created by domi on 04.08.15.
 */
public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(SpotifyStreamerArtist.ARTIST_NAME, getIntent().getStringExtra(SpotifyStreamerArtist.ARTIST_NAME));
            arguments.putParcelableArrayList(SpotifyStreamerTrack.TRACK_PARCELABLE, getIntent().getParcelableArrayListExtra(SpotifyStreamerTrack.TRACK_PARCELABLE));
            arguments.putInt(SpotifyStreamerTrack.POSITION, getIntent().getIntExtra(SpotifyStreamerTrack.POSITION, 0));

            PlayerFragment playerFragment = new PlayerFragment();
            playerFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_container, playerFragment)
                    .commit();
        }
    }
}
