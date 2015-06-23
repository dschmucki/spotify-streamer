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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by domi on 15.06.15.
 */
public class SearchArtistResultAdapter<A> extends ArrayAdapter<Artist> {

    public SearchArtistResultAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.search_artist_result, null);
        }

        Artist artist = getItem(position);

        if (artist != null) {
            ImageView artistImageView = (ImageView) convertView.findViewById(R.id.artist_search_result_image_view);
            TextView artistNameTextView = (TextView) convertView.findViewById(R.id.artist_name_search_result_text_view);

            if (artistImageView != null) {
                List<Image> images = artist.images;
                if (images != null && !images.isEmpty()) {
                    Picasso.with(getContext()).load(images.get(0).url).fit().centerCrop().into(artistImageView);
                }
            }

            if (artistNameTextView != null) {
                artistNameTextView.setText(artist.name);
            }
        }

        return convertView;

    }
}
