package com.example.android.spotifystreamer;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;
import com.example.android.spotifystreamer.data.SpotifyStreamerTrack;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by domi on 04.08.15.
 */
public class PlayerFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private TextView artistTextView;
    private TextView albumTextView;
    private ImageView albumImageView;
    private TextView trackTextView;
    private SeekBar seekBar;
    private TextView startTextView;
    private TextView endTextView;
    private ImageButton previousButton;
    private ImageButton playButton;
    private ImageButton nextButton;

    private String artist;
    private ArrayList<SpotifyStreamerTrack> spotifyStreamerTracks;
    private int position;

    private MediaPlayer mediaPlayer;
    private Timer timer;
    private boolean startPlaying = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            artist = arguments.getString(SpotifyStreamerArtist.ARTIST_NAME);
            spotifyStreamerTracks = arguments.getParcelableArrayList(SpotifyStreamerTrack.TRACK_PARCELABLE);
            position = arguments.getInt(SpotifyStreamerTrack.POSITION);
            startPlaying = true;
        }

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        artistTextView = (TextView) rootView.findViewById(R.id.player_artist_name_text_view);

        albumTextView = (TextView) rootView.findViewById(R.id.player_album_text_view);

        albumImageView = (ImageView) rootView.findViewById(R.id.player_image_view);

        trackTextView = (TextView) rootView.findViewById(R.id.track_text_view);

        seekBar = (SeekBar) rootView.findViewById(R.id.player_seek_bar);
        seekBar.setOnSeekBarChangeListener(this);

        startTextView = (TextView) rootView.findViewById(R.id.player_start_time_text_view);
        endTextView = (TextView) rootView.findViewById(R.id.player_end_time_text_view);

        previousButton = (ImageButton) rootView.findViewById(R.id.player_previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    position--;
                    setContent();
                    setupMediaPlayer();
                }
            }
        });

        playButton = (ImageButton) rootView.findViewById(R.id.player_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        nextButton = (ImageButton) rootView.findViewById(R.id.player_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < spotifyStreamerTracks.size() - 1) {
                    position++;
                    setContent();
                    setupMediaPlayer();
                }
            }
        });

        if (mediaPlayer == null) {
            setupMediaPlayer();
        }

        setContent();

        return rootView;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    private void setContent() {
        artistTextView.setText(artist);
        albumTextView.setText(spotifyStreamerTracks.get(position).getAlbumName());
        Picasso.with(getActivity()).load(spotifyStreamerTracks.get(position).getLargeImageUrl()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(albumImageView);
        trackTextView.setText(spotifyStreamerTracks.get(position).getTrackName());

        playButton.setImageResource(android.R.drawable.ic_media_play);
        playButton.setEnabled(false);
    }

    private void setupMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if (timer != null) {
            timer.cancel();
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(spotifyStreamerTracks.get(position).getPreviewUrl());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playButton.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mediaPlayer.pause();
            playButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playButton.setEnabled(true);
        int duration = mediaPlayer.getDuration();
        endTextView.setText(millisecondsToMinutesAndSeconds(duration));

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                int currentPosition = mediaPlayer.getCurrentPosition();
                                int toEnd = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
                                seekBar.setMax(mediaPlayer.getDuration());
                                seekBar.setProgress(currentPosition);
                                startTextView.setText(millisecondsToMinutesAndSeconds(currentPosition));
                                endTextView.setText("-" + millisecondsToMinutesAndSeconds(toEnd));
                            } else {
                                cancel();
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 40);
        if (startPlaying) {
            play();
            startPlaying = false;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setContent();
    }

    private String millisecondsToMinutesAndSeconds(int milliseconds) {
        return String.format(Locale.ENGLISH, "%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MICROSECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean isDismissible = false;

    @Override
    public void dismiss() {
        isDismissible = true;
        super.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (isDismissible) {
            super.onDismiss(dialog);
        }
    }
}
