package com.android.mp.wastickermaster;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.mp.wastickermaster.StickerPackListActivity.NO_INTERNET_CONNCTION_MESSAGE;
import static com.android.mp.wastickermaster.StickerPackListActivity.NO_INTERNET_CONNECTION;

/**
 * Created by Meet Patel on 10-11-2018.
 */

public class StickerListAdapter extends RecyclerView.Adapter<StickerListAdapter.StickerPackViewHolder> {
    private Activity context;
    private List<StickerPack> packList;
    private StickerDownloadTask stickerDownloadTask;

    public StickerListAdapter(Activity context, List<StickerPack> packList) {
        this.context = context;
        this.packList = packList;
    }

    @NonNull
    @Override
    public StickerPackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sticker_packlist_item, parent, false);
        return (new StickerPackViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull StickerPackViewHolder stickerPackViewHolder, int position) {
        StickerPack stickerPack = packList.get(position);
        stickerPackViewHolder.pack_name.setText(stickerPack.getName());
        stickerPackViewHolder.publisher.setText(stickerPack.getPublisher());
        stickerPackViewHolder.noOfStikcersTv.setText(new StringBuilder().append(stickerPack.getNoOfSticker()).append(" Stickers").toString());

        GlideApp
                .with(context)
                .load(stickerPack.getTrayImgUrl())
                .placeholder(R.drawable.image_before_stickerload)
                .error(R.drawable.stickerload_error)
                .dontAnimate()
                .into(stickerPackViewHolder.pack_icon);

        stickerPackViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StickerActivity.class);
                intent.putExtra("stickerPack",stickerPack);
                context.startActivity(intent);
            }
        });
        setAddButtonAppearance(stickerPackViewHolder.addToWhatsApp,stickerPack);
    }

    private void setAddButtonAppearance(ImageView addButton, StickerPack pack) {
        if (pack.getIsWhitelisted()) {
            addButton.setImageResource(R.drawable.added_to_whatsapp);
            addButton.setClickable(false);
            addButton.setOnClickListener(null);
        }
        else{
            addButton.setImageResource(R.drawable.add_to_whatsapp);
            onAddButtonClicked(addButton,pack);
        }
    }

    private void onAddButtonClicked(ImageView button,StickerPack stickerPack){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Permissions permissions = new Permissions(context);
                Connection connection = new Connection(context);
                if (!permissions.storagePermission.checkStoragePermission()) {
                    permissions.storagePermission.requestStoragePermission();
                } else {
                    if(!connection.connectionCheck.isConnected()){
                        Dialog dialog = new Dialog(context);
                        dialog.DialogShow(NO_INTERNET_CONNECTION,NO_INTERNET_CONNCTION_MESSAGE).show();
                    }
                    else {
                        stickerDownloadTask = new StickerDownloadTask(stickerPack, context);
                        stickerDownloadTask.download();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    public class StickerPackViewHolder extends RecyclerView.ViewHolder {
        ImageView pack_icon;
        TextView pack_name;
        TextView publisher;
        TextView dot;
        TextView noOfStikcersTv;
        ImageView addToWhatsApp;
        ConstraintLayout constraintLayout;

        public StickerPackViewHolder(@NonNull View itemView) {
            super(itemView);
            pack_icon = itemView.findViewById(R.id.pack_icon);
            pack_name = itemView.findViewById(R.id.pack_name);
            publisher = itemView.findViewById(R.id.publisher);
            dot = itemView.findViewById(R.id.sticker_pack_list_item_dot);
            noOfStikcersTv = itemView.findViewById(R.id.no_of_stickers);
            addToWhatsApp = itemView.findViewById(R.id.addToWhatsApp);
            constraintLayout = itemView.findViewById(R.id.sticker_pack_list_layout);
        }
    }

    public void setStickerPackList(List<StickerPack> stickerPackList) {
        this.packList = stickerPackList;
    }

}
