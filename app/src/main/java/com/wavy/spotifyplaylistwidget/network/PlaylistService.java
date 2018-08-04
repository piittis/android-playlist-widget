package com.wavy.spotifyplaylistwidget.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface PlaylistService {

    // POJO classes that responses get parsed to.
    class PlaylistResponseModel {

        public int limit;
        public int offset;
        public int total;

        @SerializedName("items")
        public ArrayList<PlaylistModel> playlists;
    }

    class PlaylistModel {

        public String name;
        public String id;
        public String uri;
        public OwnerModel owner;
        public TracksModel tracks;
        public ArrayList<ImageModel> images;
    }

    class TracksModel {
        public int total;
    }

    class ImageModel {
        public int height;
        public int width;
        public String url;
    }

    class OwnerModel {
        public String id;
    }

    @GET("me/playlists?limit=50")
    Call<PlaylistResponseModel> getPlaylistsOfUser(@Header("Authorization") String authHeader,
                                                    @Query("offset") int offset);

}
