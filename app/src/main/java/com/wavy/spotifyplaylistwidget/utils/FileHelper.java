package com.wavy.spotifyplaylistwidget.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class FileHelper {

    // todo: get rid of this when all users are on dqlite
    public static void writeString(Context context, String fileName, String fileContent) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(fileContent);
            writer.close();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public static String readString(Context context, String fileName) {

        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();
            return sb.toString();

        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return null;
    }

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
                        savePng(callingActivity, pl.id, image);
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
