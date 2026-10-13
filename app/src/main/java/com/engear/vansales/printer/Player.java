package com.engear.vansales.printer;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcelable;

import com.google.android.gms.common.data.Freezable;

/**
 * Created by Rakshit on 06-Feb-17.
 */
public interface Player extends Parcelable, Freezable<Player> {
    String getDisplayName();

    void getDisplayName(CharArrayBuffer charArrayBuffer);

    Uri getHiResImageUri();

    @Deprecated
    String getHiResImageUrl();

    Uri getIconImageUri();

    @Deprecated
    String getIconImageUrl();

    long getLastPlayedWithTimestamp();

    String getPlayerId();

    long getRetrievedTimestamp();

    int gh();

    boolean hasHiResImage();

    boolean hasIconImage();
}

