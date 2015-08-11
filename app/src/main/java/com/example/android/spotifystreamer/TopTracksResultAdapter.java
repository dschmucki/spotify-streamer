package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.data.SpotifyStreamerTrack;
import com.squareup.picasso.Picasso;

/**
 * Created by domi on 15.06.15.
 */
public class TopTracksResultAdapter<A> extends ArrayAdapter<SpotifyStreamerTrack> {


    public TopTracksResultAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.list_item_top_tracks, null);
        }

        SpotifyStreamerTrack spotifyStreamerTrack = getItem(position);

        if (spotifyStreamerTrack != null) {
            ImageView albumImageView = (ImageView) convertView.findViewById(R.id.top_tracks_result_image_view);
            TextView trackNameTextView = (TextView) convertView.findViewById(R.id.top_tracks_result_track_text_view);
            TextView albumNameTextView = (TextView) convertView.findViewById(R.id.top_tracks_result_album_text_view);

            if (albumImageView != null) {
                Picasso.with(getContext()).load(spotifyStreamerTrack.getSmallImageUrl()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(albumImageView);
            }

            if (trackNameTextView != null) {
                trackNameTextView.setText(spotifyStreamerTrack.getTrackName());
            }

            if (albumNameTextView != null) {
                albumNameTextView.setText(spotifyStreamerTrack.getAlbumName());
            }
        }
        return convertView;
    }
}
