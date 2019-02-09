package com.wavy.spotifyplaylistwidget.db.converter;

import org.threeten.bp.Instant;

import androidx.room.TypeConverter;


public class InstantConverter {

    @TypeConverter
    public static Instant toInstant(long epochSeconds) {
        return Instant.ofEpochSecond(epochSeconds);
    }

    @TypeConverter
    public static long toLong(Instant instant) {
        // Second precision is fine.
        return instant.getEpochSecond();
    }
}
