package com.wavy.spotifyplaylistwidget.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import com.wavy.spotifyplaylistwidget.db.entity.PlayableEntity;

import java.util.List;

@Dao
public abstract class PlayableDao {

    @Update
    abstract void UpdateAll(List<PlayableEntity> playables);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertIfNotExist(List<PlayableEntity> playables);

    @Transaction
    public void upsertAll(List<PlayableEntity> playables) {
        UpdateAll(playables);
        insertIfNotExist(playables);
    }

}
