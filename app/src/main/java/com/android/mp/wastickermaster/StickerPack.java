package com.android.mp.wastickermaster;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Meet Patel on 10-11-2018.
 */

public class StickerPack implements Parcelable {
    public String identifier;
    public String name;
    public String publisher;
    public String tray_image_file;

    public int noOfSticker;

    public String trayFileName;
    String publisherEmail = null;
    String publisherWebsite = null;
    String privacyPolicyWebsite = null;
    String licenseAgreementWebsite = null;


    /*----------------------------------*/

    private List<Sticker> stickers;
    private long totalSize;
    String androidPlayStoreLink;
    public boolean isWhitelisted;


    public StickerPack(){

    }

    public StickerPack(String identifier, String name, String publisher, String trayFileName, String publisherEmail, String publisherWebsite, String privacyPolicyWebsite, String licenseAgreementWebsite) {
        this.identifier = identifier;
        this.name = name;
        this.publisher = publisher;
        this.trayFileName = trayFileName;
        this.publisherEmail = publisherEmail;
        this.publisherWebsite = publisherWebsite;
        this.privacyPolicyWebsite = privacyPolicyWebsite;
        this.licenseAgreementWebsite = licenseAgreementWebsite;
    }

    public StickerPack(String identifier, String pack_name, String publisher, String tray_image_file) {
        this.identifier = identifier;
        this.name = pack_name;
        this.publisher=publisher;
        this.tray_image_file=tray_image_file;
    }

    protected StickerPack(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        publisher = in.readString();
        tray_image_file = in.readString();
        noOfSticker  = in.readInt();
        publisherEmail = in.readString();
        publisherWebsite = in.readString();
        privacyPolicyWebsite = in.readString();
        licenseAgreementWebsite = in.readString();
        stickers = in.createTypedArrayList(Sticker.CREATOR);
        totalSize = in.readLong();
        androidPlayStoreLink = in.readString();
        isWhitelisted = in.readByte() != 0;
    }

    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        @Override
        public StickerPack createFromParcel(Parcel in) {
            return new StickerPack(in);
        }

        @Override
        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };

    public String getIdentifier() {
        return identifier;
    }

    public void setNoOfSticker(int noOfSticker) {
        this.noOfSticker = noOfSticker;
    }

    public String getTrayImgUrl() {
        return tray_image_file;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getNoOfSticker() {
        return noOfSticker;
    }

    void setIsWhitelisted(boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    boolean getIsWhitelisted() {
        return isWhitelisted;
    }

    public String getTrayFileName() {
        return trayFileName;
    }

    public void setTrayFileName(String trayFileName) {
        this.trayFileName = trayFileName;
    }


    void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
        totalSize = 0;
        for (Sticker sticker : stickers) {
            totalSize += sticker.size;
        }
    }

    public void setAndroidPlayStoreLink(String androidPlayStoreLink) {
        this.androidPlayStoreLink = androidPlayStoreLink;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(identifier);
        parcel.writeString(name);
        parcel.writeString(publisher);
        parcel.writeString(tray_image_file);
        parcel.writeInt(noOfSticker);
        parcel.writeString(publisherEmail);
        parcel.writeString(publisherWebsite);
        parcel.writeString(privacyPolicyWebsite);
        parcel.writeString(licenseAgreementWebsite);
        parcel.writeTypedList(stickers);
        parcel.writeLong(totalSize);
        parcel.writeString(androidPlayStoreLink);
        parcel.writeByte((byte) (isWhitelisted ? 1 : 0));
    }
}

