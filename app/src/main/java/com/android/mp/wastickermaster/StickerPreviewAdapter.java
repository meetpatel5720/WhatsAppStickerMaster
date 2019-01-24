package com.android.mp.wastickermaster;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Meet Patel on 13-11-2018.
 */

public class StickerPreviewAdapter extends ArrayAdapter {
    private Activity context;
    private List<Sticker> stickerList;

    public StickerPreviewAdapter(Activity context, List<Sticker> stickerList) {
        super(context, R.layout.sticker_prewiew,stickerList);
        this.context = context;
        this.stickerList = stickerList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View stickerListItem = inflater.inflate(R.layout.sticker_prewiew, null, true);

        ImageView stickerPreview = (ImageView)stickerListItem.findViewById(R.id.sticker_preview);

        Sticker stickerClass=stickerList.get(position);
        GlideApp
                .with(context)
                .load(stickerClass.getstickerImageUrl())
                .placeholder(R.drawable.image_before_stickerload)
                .error(R.drawable.stickerload_error)
                .dontAnimate()
                .into(stickerPreview);

        return stickerListItem;
    }
}
