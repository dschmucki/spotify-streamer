package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by domi on 15.06.15.
 */
public class TopTracksResultAdapter<A> extends ArrayAdapter<Track> {

    public static int SMALL_IMAGE_SIZE = 200;

    public TopTracksResultAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.top_tracks_result, null);
        }

        Track track = getItem(position);

        if (track != null) {
            ImageView albumImageView = (ImageView) convertView.findViewById(R.id.top_tracks_result_image_view);
            TextView trackNameTextView = (TextView) convertView.findViewById(R.id.top_tracks_result_track_text_view);
            TextView albumNameTextView = (TextView) convertView.findViewById(R.id.top_tracks_result_album_text_view);

            if (albumImageView != null) {
                List<Image> images = track.album.images;
                if (images != null && !images.isEmpty()) {
                    String url = getSmallImageUrl(images);
                    Picasso.with(getContext()).load(url).fit().centerCrop().into(albumImageView);
                }
            }

            if (trackNameTextView != null) {
                trackNameTextView.setText(track.name);
            }

            if (albumNameTextView != null) {
                albumNameTextView.setText(track.album.name);
            }
        }

        return convertView;

    }

    private String getSmallImageUrl(final List<Image> images) {
        for (Image i : images) {
            if (i.height == SMALL_IMAGE_SIZE || i.width == SMALL_IMAGE_SIZE) {
                return i.url;
            }
        }
        return images.get(0).url;
    }
}
