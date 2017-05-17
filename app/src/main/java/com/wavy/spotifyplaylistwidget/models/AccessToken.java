package com.wavy.spotifyplaylistwidget.models;

import java.util.Date;

public class AccessToken {

    public String token;
    public Date expireDate;

    public boolean isvalid() {

        long fiveMinsMs = 1000 * 60 * 5;
        long expireDateMs = expireDate.getTime();

        long fiveMinutesBeforeExpire = expireDateMs - fiveMinsMs;
        long currentTime = System.currentTimeMillis();

        //if token is still valid more than 5 minutes, it is valid
        return currentTime < fiveMinutesBeforeExpire;
    }
}
