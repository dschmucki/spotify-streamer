package com.example.android.spotifystreamer.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@link Parcelable} version of {@link kaaes.spotify.webapi.android.models.Artist}
 *
 * @author dschmucki
 */
public class SpotifyStreamerArtist implements Parcelable {

    public static final String ARTIST_NAME = "artistName";
    public static final String SPOTIFY_ID = "spotifyId";

    String artistName;
    String spotifyId;
    String imageUrl;

    public SpotifyStreamerArtist(final String artistName, final String spotifyId, final String imageUrl) {
        this.artistName = artistName;
        this.spotifyId = spotifyId;
        this.imageUrl = imageUrl;
    }

    protected SpotifyStreamerArtist(Parcel in) {
        artistName = in.readString();
        spotifyId = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(spotifyId);
        dest.writeString(imageUrl);
    }

    public static final Parcelable.Creator<SpotifyStreamerArtist> CREATOR = new Parcelable.Creator<SpotifyStreamerArtist>() {

        @Override
        public SpotifyStreamerArtist createFromParcel(Parcel source) {
            return new SpotifyStreamerArtist(source);
        }

        @Override
        public SpotifyStreamerArtist[] newArray(int size) {
            return new SpotifyStreamerArtist[size];
        }
    };

    public String getArtistName() {
        return artistName;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
