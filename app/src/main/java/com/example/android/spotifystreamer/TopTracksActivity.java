package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by domi on 17.06.15.
 */
public class TopTracksActivity extends AppCompatActivity implements TopTracksView, AdapterView.OnItemClickListener {

    private TopTracksPresenter topTracksPresenter;
    private TopTracksResultAdapter<Track> topTracksResultAdapter;

    private ListView topTracksResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topTracksPresenter = TopTracksPresenter.getInstance();
        topTracksPresenter.initialize();
        topTracksPresenter.setTopTracksView(this);
        setContentView(R.layout.top_tracks_activity);

        topTracksResultAdapter = new TopTracksResultAdapter<>(this, R.layout.top_tracks_result);

        topTracksResultListView = (ListView) findViewById(R.id.top_tracks_list_view);
        topTracksResultListView.setAdapter(topTracksResultAdapter);
        topTracksResultListView.setOnItemClickListener(this);

        Intent intent = getIntent();
        topTracksPresenter.searchTopTracks(intent.getStringExtra(SearchArtistPresenter.ARTIST_ID));
        getSupportActionBar().setSubtitle(intent.getStringExtra(SearchArtistPresenter.ARTIST_NAME));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        topTracksPresenter.setTopTracksView(null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        topTracksPresenter.resume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void showEmpty() {
        topTracksResultAdapter.clear();
    }

    @Override
    public void showNoSearchResults() {
        topTracksResultAdapter.clear();
        Toast.makeText(TopTracksActivity.this, R.string.top_tracks_no_results, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showSearchResults(List<Track> tracks) {
        topTracksResultAdapter.clear();
        topTracksResultAdapter.addAll(tracks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        topTracksPresenter.backPressed();
    }


}
