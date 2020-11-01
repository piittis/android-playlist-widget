package com.wavy.spotifyplaylistwidget.db.converter;

import androidx.room.TypeConverter;

import com.wavy.spotifyplaylistwidget.db.entity.PlayableType;

public class PlayableTypeConverter {

    @TypeConverter
    public static PlayableType toPlayableType(int type) {
        switch (type) {
            case 1: return PlayableType.PLAYLIST;
            case 2: return PlayableType.ALBUM;
            case 3: return PlayableType.ARTIST;
            default: return PlayableType.UNKNOWN;
        }
    }

    @TypeConverter
    public static int toInt(PlayableType type) {
        switch (type) {
            case PLAYLIST: return 1;
            case ALBUM: return 2;
            case ARTIST: return 3;
            default: return -1;
        }
    }
}
