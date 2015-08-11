package com.example.android.spotifystreamer.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@link Parcelable} version of {@link kaaes.spotify.webapi.android.models.Track}
 *
 * @author dschmucki
 */
public class SpotifyStreamerTrack implements Parcelable {

    public static final String TRACK_PARCELABLE = "trackParcelable";
    public static final String POSITION = "position";

    String trackName;
    String albumName;
    String smallImageUrl;
    String largeImageUrl;
    String previewUrl;

    public SpotifyStreamerTrack(final String trackName, final String albumName, final String smallImageUrl, final String largeImageUrl, final String previewUrl) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.smallImageUrl = smallImageUrl;
        this.largeImageUrl = largeImageUrl;
        this.previewUrl = previewUrl;
    }

    protected SpotifyStreamerTrack(Parcel in) {
        trackName = in.readString();
        albumName = in.readString();
        smallImageUrl = in.readString();
        largeImageUrl = in.readString();
        previewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(smallImageUrl);
        dest.writeString(largeImageUrl);
        dest.writeString(previewUrl);
    }

    public static final Creator<SpotifyStreamerTrack> CREATOR = new Creator<SpotifyStreamerTrack>() {

        @Override
        public SpotifyStreamerTrack createFromParcel(Parcel source) {
            return new SpotifyStreamerTrack(source);
        }

        @Override
        public SpotifyStreamerTrack[] newArray(int size) {
            return new SpotifyStreamerTrack[size];
        }
    };

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
}
