package com.wavy.spotifyplaylistwidget.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.wavy.spotifyplaylistwidget.db.entity.PlayableEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlayables;

import java.util.List;

@Dao
public abstract class WidgetPlayablesDao {

    @Query("delete from widget_playables where widgetId = :widgetId")
    abstract void deleteByWidgetId(int widgetId);

    @Insert
    abstract void insertAll(List<WidgetPlayables> widgetPlayables);

    @Transaction
    public void setWidgetsPlaylists(int widgetId, List<WidgetPlayables> widgetPlayables) {
        deleteByWidgetId(widgetId);
        insertAll(widgetPlayables);
    }

    @Query("select pl.* from widget_playables wp " +
            "join playables pl on (pl.spotifyId = wp.playableId) " +
            "where wp.widgetId = :androidWidgetId " +
            "order by wp.playablePosition asc")
    public abstract List<PlayableEntity> getWidgetPlayables(int androidWidgetId);

    @Query("select pl.spotifyId from widget_playables wp " +
            "join playables pl on (pl.spotifyId = wp.playableId) " +
            "where wp.widgetId = :androidWidgetId " +
            "order by wp.playablePosition asc")
    public abstract List<String> getWidgetPlayableIds(int androidWidgetId);

    @Query("select wp.* from widget_playables wp where wp.widgetId = :androidWidgetId order by wp.playablePosition")
    public abstract List<WidgetPlayables> getByWidgetId(int androidWidgetId);

}
