package com.wavy.spotifyplaylistwidget.persistence;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileHelper {

    public interface OnCompleteCallbackListener {
        void onComplete();
    }

    public static void writeString(Context context, String fileName, String fileContent) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(fileContent);
            writer.close();
        } catch (Exception e) {
            // todo handle errors better
            e.printStackTrace();
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
            // todo handle errors better
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Persists the images for given playlists. File name will be the id of the playlist.
     */
    public static void persistPlaylistImages(Activity callingActivity, ArrayList<PlaylistViewModel> playlists,
                                             OnCompleteCallbackListener onCompleteCallbackListener) {

        // Process images in parallel using ExecutorService.
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        int imageSize = callingActivity.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);

        // Make a callable for each image operation
        ArrayList<Callable<Object>> callables = new ArrayList<>(playlists.size());
        for (PlaylistViewModel pl : playlists) {
            callables.add(Executors.callable(() -> {
                try {
                    Bitmap image = Picasso.with(callingActivity).load(pl.imageUrl).resize(imageSize, imageSize).get();
                    savePng(callingActivity, pl.id, image);
                } catch (IOException e) {
                    // todo recover from this
                    e.printStackTrace();
                }
            }));
        }

        // Execute in AsyncTask to not block the UI thread.
        AsyncTask.execute(() -> {
            try {
                // Execute callables in parallel.
                threadPoolExecutor.invokeAll(callables);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callingActivity.runOnUiThread(onCompleteCallbackListener::onComplete);
            Log.d("runnable", "done");
        });

    }

    private static void savePng(Context context, String fileName, Bitmap bitmap) {

        try {
            FileOutputStream outputStream = context.openFileOutput(fileName + ".png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
