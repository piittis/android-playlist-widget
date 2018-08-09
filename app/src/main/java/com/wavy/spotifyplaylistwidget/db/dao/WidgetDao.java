package com.wavy.spotifyplaylistwidget.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;

import java.util.List;

@Dao
public abstract class WidgetDao {

    @Query("select * from widgets where androidWidgetId = :androidWidgetId limit 1")
    public abstract WidgetEntity getById(int androidWidgetId);

    @Query("delete from widgets where androidWidgetId = :androidWidgetId")
    public abstract void deleteById(int androidWidgetId);

    @Update
    abstract void update(WidgetEntity widget);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertIfNotExist(WidgetEntity widget);

    @Transaction
    public void upsert(WidgetEntity widget) {
        update(widget);
        insertIfNotExist(widget);
    }
}