package com.example.android.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by domi on 17.06.15.
 */
public class SearchArtistActivity extends AppCompatActivity implements ArtistView, TextWatcher, AdapterView.OnItemClickListener {

    private static final String LOG_TAG = SearchArtistActivity.class.getSimpleName();

    private SearchArtistPresenter searchArtistPresenter;
    private SearchArtistResultAdapter<Artist> searchArtistResultAdapter;

    private EditText searchArtistEditText;
    private ListView searchArtistResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchArtistPresenter = SearchArtistPresenter.getInstance();
        searchArtistPresenter.initialize();
        searchArtistPresenter.setArtistView(this);
        setContentView(R.layout.search_artist_activity);

        searchArtistEditText = (EditText) findViewById(R.id.search_artist_edit_text);
        searchArtistEditText.addTextChangedListener(this);

        searchArtistResultAdapter = new SearchArtistResultAdapter<>(this, R.layout.search_artist_result);

        searchArtistResultListView = (ListView) findViewById(R.id.search_artist_result_list_view);
        searchArtistResultListView.setAdapter(searchArtistResultAdapter);
        searchArtistResultListView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchArtistPresenter.setArtistView(null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        searchArtistPresenter.resume();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        searchArtistPresenter.searchArtist(s.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG, "Clicked Artist ID: " + searchArtistResultAdapter.getItem(position).id);
        searchArtistPresenter.showTopTracks(this, searchArtistResultAdapter.getItem(position));
    }

    @Override
    public void showEmpty() {
        searchArtistResultAdapter.clear();
    }

    @Override
    public void showNoSearchResults() {
        searchArtistResultAdapter.clear();
        Toast.makeText(SearchArtistActivity.this, R.string.no_results_found_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        // TODO: Implement loading dialog
    }

    @Override
    public void hideLoading() {
        // TODO: Hide Loading dialog
    }

    @Override
    public void showSearchResults(final List<Artist> artists) {
        searchArtistResultAdapter.clear();
        searchArtistResultAdapter.addAll(artists);
    }

    @Override
    public void showSearchText(String text) {
        searchArtistEditText.setText(text);
    }
}
