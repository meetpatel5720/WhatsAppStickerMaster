package com.android.mp.wastickermaster;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("title")
    public String title;
    @SerializedName("content")
    public String content;

    public Message(){

    }
}
