package com.wavy.spotifyplaylistwidget.db.converter;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.Instant;


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
