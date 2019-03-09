package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.utils.PicassoOnScrollListener;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;

public class SelectActivity extends PlaylistWidgetConfigureActivityBase {

    private static final String TAG = "SelectActivity";

    // view elements
    @BindView(R.id.playlist_selection_list) public RecyclerView mPlaylistsSelectionView;
    @BindView(R.id.selection_next_button) Button mNextButton;
    @BindView(R.id.selection_toolbar) Toolbar mToolbar;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mSwipeRefresh;

    private PlaylistSelectionAdapter mPlaylistSelectionAdapter;
    private String mToolbarTitle;

    private CompositeDisposable playlistSubscriptions;

    @Inject
    SpotifyApi mSpotifyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IoC.getInjector().inject(this);

        setContentView(R.layout.activity_select);

        ButterKnife.bind(this);
        mToolbarTitle = getResources().getString(R.string.select_playlists);
        mToolbar.setTitle(mToolbarTitle);
        setSupportActionBar(mToolbar);

        mNextButton.setOnClickListener((v) -> startArrangeActivity());

        mSwipeRefresh.setOnRefreshListener(this::manualUpdate);

        if (isFirstCreate) {
            mPlaylists.clearPlaylists();
        } else if (mPlaylists.getSelectedPlaylistsCount() > 0) {
            mNextButton.setEnabled(true);
        }

        initializePlaylistSelectionList();

        if (isFirstCreate) {
            LoadPlaylistsAfterAuth();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playlistSubscriptions != null && !playlistSubscriptions.isDisposed()) {
            playlistSubscriptions.dispose();
        }
    }

    private void initializePlaylistSelectionList() {
        mPlaylistSelectionAdapter = new PlaylistSelectionAdapter(mPlaylists.getPlaylists(), this);
        mPlaylistsSelectionView.setAdapter(mPlaylistSelectionAdapter);
        mPlaylistsSelectionView.setLayoutManager(new LinearLayoutManager(this));
        mPlaylistsSelectionView.addOnScrollListener(new PicassoOnScrollListener());
        mPlaylistSelectionAdapter.setOnClickListener((v) -> updateSelectedPlaylists());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selectionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                selectAll(true);
                return true;
            case R.id.remove_selections:
                selectAll(false);
                return true;
            case R.id.refresh:
                mSwipeRefresh.setRefreshing(true);
                manualUpdate();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void LoadPlaylistsAfterAuth() {
        if (mSpotifyApi.isAccessTokenValid()) {
            loadPlaylists();
        } else {
            // Do authentication first, playlists will be loaded in onActivityResult instead.
            doAuthentication();
        }
    }

    private void loadPlaylists() {

        if (mPlaylists.getPlaylistsCount() == 0) {
            showSpinner();
            mPlaylistsSelectionView.scheduleLayoutAnimation();
        }

        // Dispose any ongoing playlist loading.
        if (playlistSubscriptions != null && !playlistSubscriptions.isDisposed()) {
            playlistSubscriptions.dispose();
        }

        ConnectableObservable<ArrayList<PlaylistViewModel>> playlistObservable = mSpotifyApi.getPlaylists()
                .observeOn(AndroidSchedulers.mainThread()).publish();

        playlistSubscriptions = new CompositeDisposable(

                // Handle first results.
                playlistObservable.first(new ArrayList<>())
                        .subscribe(result -> {
                            mSwipeRefresh.setRefreshing(false);
                            hideSpinner();

                            mPlaylists.initializePlaylists(result);
                            mPlaylistSelectionAdapter.notifyDataSetChanged();

                        }, this::onSpotifyApiError),

                // Handle rest of the results.
                playlistObservable.skip(1)
                        .subscribe(result -> {

                            int firstNewItem = mPlaylists.getPlaylistsCount();
                            mPlaylists.addPlaylists(result);

                            for(int i = firstNewItem; i < firstNewItem + result.size(); i++) {
                                mPlaylistSelectionAdapter.notifyItemChanged(i);
                            }

                        }, this::onSpotifyApiError)
        );

        // Start the data fetch.
        playlistObservable.connect();
    }

    private void startArrangeActivity() {
        Intent intent = new Intent(getApplicationContext(), ArrangeActivity.class);
        startNextConfigurationActivity(intent);
    }

    private void manualUpdate() {
        logEvent("manual_data_refresh");
        LoadPlaylistsAfterAuth();
    }

    private void selectAll(boolean selected) {
        logEvent((selected) ? "select_all" : "remove_selections");

        if (mPlaylists != null && mPlaylistSelectionAdapter != null) {
            for (PlaylistViewModel pl : mPlaylists.getPlaylists()) {
                pl.selected = selected;
            }
            mPlaylistSelectionAdapter.notifyDataSetChanged();
            updateSelectedPlaylists();
        }
    }

    private void updateSelectedPlaylists() {

        mPlaylists.updateSelectedPlaylists();
        if (mPlaylists.getSelectedPlaylistsCount() > 0) {
            mToolbar.setTitle(String.format(getString(R.string.playlists_selected_count),
                    mPlaylists.getSelectedPlaylistsCount(), mPlaylists.getPlaylistsCount()));

            mNextButton.setEnabled(true);
        } else {
            mToolbar.setTitle(mToolbarTitle);
            mNextButton.setEnabled(false);
        }
    }

    private void showSpinner() {
        this.findViewById(R.id.playlists_loading_indicator).setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        this.findViewById(R.id.playlists_loading_indicator).setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult " + requestCode);
        super.onActivityResult(requestCode, resultCode, intent);

        // Authentication done.
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {
            loadPlaylists();
        }
    }

    public void onSpotifyApiError(Throwable error) {
        Crashlytics.logException(error);
        logEvent("api_error");
        Toast.makeText(getApplicationContext(), getString(R.string.spotify_api_error) + " (" + error.getMessage() + ")"
                , Toast.LENGTH_LONG).show();
    }
}