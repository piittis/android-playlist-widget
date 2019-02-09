package com.wavy.spotifyplaylistwidget.db.dao;


import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class PlaylistDao {

    @Update
    abstract void UpdateAll(List<PlaylistEntity> playlists);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertIfNotExist(List<PlaylistEntity> playlists);

    @Transaction
    public void upsertAll(List<PlaylistEntity> playlists) {
        UpdateAll(playlists);
        insertIfNotExist(playlists);
    }

}
