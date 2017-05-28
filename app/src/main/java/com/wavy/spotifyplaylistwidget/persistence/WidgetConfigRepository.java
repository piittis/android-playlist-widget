package com.wavy.spotifyplaylistwidget.persistence;

import com.wavy.spotifyplaylistwidget.widget.WidgetConfigModel;

public interface WidgetConfigRepository {

    void put(int appWidgetId, WidgetConfigModel configToWrite);
    void remove(int appWidgetId);
    WidgetConfigModel get(int appWidgetId);

}
