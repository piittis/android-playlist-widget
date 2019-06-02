package com.wavy.spotifyplaylistwidget.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.io.FileOutputStream;
import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class FileHelper {

    /**
     * Persists the images for given playlists. File name will be the id of the playlist.
     * Files will go the the root folder.
     */
    public Completable persistPlaylistImages(Activity callingActivity, ArrayList<PlaylistViewModel> playlists) {

        int imageSize = callingActivity.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);

        return Flowable.fromIterable(playlists)
                .parallel()
                .runOn(Schedulers.computation())
                .doOnNext(pl -> {
                    try {
                        Bitmap image = Picasso.get().load(pl.imageUrl).resize(imageSize, imageSize).get();
                        if (image != null) {
                            savePng(callingActivity, pl.id, image);
                        }
                    } catch(Exception e) {
                        Crashlytics.log("Error loading playlist image");
                        Crashlytics.logException(e);
                    }
                })
                .sequential()
                .ignoreElements();
    }

    private static void savePng(Context context, String fileName, Bitmap bitmap) {

        try {
            FileOutputStream outputStream = context.openFileOutput(fileName + ".png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            Crashlytics.log("Error saving playlist image");
            Crashlytics.logException(e);
        }
    }

}
