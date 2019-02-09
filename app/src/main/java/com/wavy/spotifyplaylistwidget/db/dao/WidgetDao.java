package com.wavy.spotifyplaylistwidget.db.dao;


import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

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