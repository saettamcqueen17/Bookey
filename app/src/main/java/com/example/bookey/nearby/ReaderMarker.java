package com.example.bookey.nearby;

public class ReaderMarker {
    public final String nickname;
    public final double latOffset;
    public final double lonOffset;

    public ReaderMarker(String nickname, double latOffset, double lonOffset) {
        this.nickname = nickname;
        this.latOffset = latOffset;
        this.lonOffset = lonOffset;
    }
}
