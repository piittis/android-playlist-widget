package com.wavy.spotifyplaylistwidget.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;

import java.util.List;

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
