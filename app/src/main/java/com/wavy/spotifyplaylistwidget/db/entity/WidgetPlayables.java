package com.wavy.spotifyplaylistwidget.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "widget_playables",
        primaryKeys = {"widgetId", "playableId"},
        foreignKeys = {
                @ForeignKey(entity = WidgetEntity.class,
                            parentColumns = "androidWidgetId",
                            childColumns = "widgetId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PlayableEntity.class,
                            parentColumns = "spotifyId",
                            childColumns = "playableId")
                }
)
public class WidgetPlayables {

    public int widgetId;
    @NonNull
    public String playableId;

    public int playablePosition;

    public WidgetPlayables(int widgetId, @NonNull String playableId, int playablePosition) {
        this.widgetId = widgetId;
        this.playableId = playableId;
        this.playablePosition = playablePosition;
    }

}
