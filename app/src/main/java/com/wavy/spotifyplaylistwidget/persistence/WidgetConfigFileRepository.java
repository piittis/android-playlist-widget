package com.wavy.spotifyplaylistwidget.persistence;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavy.spotifyplaylistwidget.widget.WidgetConfigModel;

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
        Log.d("write " + fileName, content);
        FileHelper.writeString(mContext, fileName, content);
    }

    @Override
    public void remove(int appWidgetId) {
        String fileName = "widget_" + appWidgetId + "_config.json";
        Log.d("removed", fileName);
        mContext.deleteFile(fileName);
    }

    @Override
    public WidgetConfigModel get(int appWidgetId) {
        String fileName = "widget_" + appWidgetId + "_config.json";
        String content = FileHelper.readString(mContext, fileName);
        Log.d("read " + fileName, content);
        return mGson.fromJson(content, WidgetConfigModel.class);
    }
}
