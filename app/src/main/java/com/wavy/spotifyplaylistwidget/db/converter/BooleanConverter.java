package com.wavy.spotifyplaylistwidget.db.converter;

import androidx.room.TypeConverter;

public class BooleanConverter {

    @TypeConverter
    public static Boolean toBoolean(int value) {
        return value != 0;
    }

    @TypeConverter
    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }
}
