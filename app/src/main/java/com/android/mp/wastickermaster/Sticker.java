package com.android.mp.wastickermaster;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Meet Patel on 12-11-2018.
 */

public class Sticker implements Parcelable {

    public String stickerFileName;
    public String sticker_url;
    public List<String> emojis;

    long size;


    public long getSize() {
        return size;
    }

    public Sticker() {

    }

    public Sticker(String sticker_url) {
        this.sticker_url = sticker_url;
        this.emojis = null;
    }

    public Sticker(List<String> emojis, String imageFile) {
        this.emojis = emojis;
        this.stickerFileName=imageFile;
    }

    public void setSticker_url(String sticker_url) {
        this.sticker_url = sticker_url;
    }

    public String getstickerImageUrl() {
        return sticker_url;
    }

    public String getStickerFileName() {
        return stickerFileName;
    }

    public void setStickerFileName(String stickerFileName) {
        this.stickerFileName = stickerFileName;
    }

    public void setSize(long size) {
        this.size = size;
    }



    protected Sticker(Parcel in) {
        sticker_url = in.readString();
        stickerFileName = in.readString();
        emojis = in.createStringArrayList();
        size = in.readLong();
    }

    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sticker_url);
        dest.writeString(stickerFileName);
        dest.writeStringList(emojis);
        dest.writeLong(size);
    }
}
