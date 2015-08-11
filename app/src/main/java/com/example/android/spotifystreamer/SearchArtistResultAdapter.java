package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.data.SpotifyStreamerArtist;
import com.squareup.picasso.Picasso;

/**
 * Created by domi on 15.06.15.
 */
public class SearchArtistResultAdapter<A> extends ArrayAdapter<SpotifyStreamerArtist> {

    public SearchArtistResultAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.list_item_search_artist, null);
        }

        SpotifyStreamerArtist spotifyStreamerArtist = getItem(position);

        if (spotifyStreamerArtist != null) {
            ImageView artistImageView = (ImageView) convertView.findViewById(R.id.artist_search_result_image_view);
            TextView artistNameTextView = (TextView) convertView.findViewById(R.id.artist_name_search_result_text_view);

            if (artistImageView != null) {
                Picasso.with(getContext()).load(spotifyStreamerArtist.getImageUrl()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(artistImageView);
            }

            if (artistNameTextView != null) {
                artistNameTextView.setText(spotifyStreamerArtist.getArtistName());
            }
        }

        return convertView;

    }
}
