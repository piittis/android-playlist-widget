package com.wavy.spotifyplaylistwidget.persistence;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;
import com.wavy.spotifyplaylistwidget.widget.WidgetConfigModel;

// TODO: remove when all users are on sqlite.
public class WidgetConfigFileRepository implements WidgetConfigRepository {

    private Gson mGson = new GsonBuilder().setPrettyPrinting().create();
    private Context mContext;

    public WidgetConfigFileRepository(Context context) {
        mContext = context;
    }

    @Override
    public void put(int appWidgetId, WidgetConfigModel configToWrite) {
        String fileName = "widget_" + appWidgetId + "_config.json";
        String content = mGson.toJson(configToWrite);
        FileHelper.writeString(mContext, fileName, content);
    }

    @Override
    public void remove(int appWidgetId) {
        String fileName = "widget_" + appWidgetId + "_config.json";
        mContext.deleteFile(fileName);
    }

    @Override
    public WidgetConfigModel get(int appWidgetId) {
        String fileName = "widget_" + appWidgetId + "_config.json";
        String content = FileHelper.readString(mContext, fileName);
        return mGson.fromJson(content, WidgetConfigModel.class);
    }
}
