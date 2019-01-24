package com.android.mp.wastickermaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.DialogInterface.*;


class StickerDownloadTask {

    private static final String DOWNLOAD_FAILED = "Download failed";
    private static final String CAN_NOT_DOWNLOAD = "Problem in downloading some stickers. Please re-download it.";
    private StickerPack stickerPack;
    private int i = 0;
    private Activity context;
    private ProgressDialog downloadProgressDialoag;
    private List<Task<?>> tasks = new ArrayList<>();
    private ArrayList<Sticker> stickerArrayList;
    private DownloadTask downloadTask;


    StickerDownloadTask(StickerPack stickerPack, Activity context) {
        this.stickerPack = stickerPack;
        this.context = context;
    }

    protected void download() {
        stickerArrayList = (ArrayList<Sticker>) stickerPack.getStickers();
        downloadTask = new DownloadTask();
        downloadTask.execute(stickerArrayList.toArray(new Sticker[stickerArrayList.size()]));
    }

    private void setDownloadProgressDialoag() {
        downloadProgressDialoag = new ProgressDialog(context,R.style.PackLoaderDialog);
        downloadProgressDialoag.setTitle(context.getString(R.string.downloading));
        downloadProgressDialoag.setMax(stickerPack.noOfSticker);
        downloadProgressDialoag.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgressDialoag.setCancelable(false);
        downloadProgressDialoag.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.download_in_background), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadProgressDialoag.dismiss();
                Toast.makeText(context, R.string.completed_in_background, Toast.LENGTH_LONG).show();
            }
        });
        downloadProgressDialoag.show();
    }


    public class DownloadTask extends AsyncTask<Sticker, Integer, List<Task<?>>> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setDownloadProgressDialoag();
        }

        @Override
        protected void onPostExecute(List<Task<?>> tasks) {
            super.onPostExecute(tasks);
            Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                @Override
                public void onSuccess(List<Object> list) {
                    downloadProgressDialoag.dismiss();
                    WriteToJSON writeToJSON = new WriteToJSON(stickerPack, context);
                    try {
                        writeToJSON.makeJsonFile();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StickerPackProvider.setMATCHER(stickerPack);
                    AddStikerPackActivity addStikerPackActivity = new AddStikerPackActivity(context);
                    addStikerPackActivity.addToWhatsApp.addStickerPackToWhatsApp(stickerPack.getIdentifier(), stickerPack.getName());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    downloadProgressDialoag.dismiss();
                    Dialog dialog = new Dialog(context);
                    dialog.DialogShow(DOWNLOAD_FAILED, CAN_NOT_DOWNLOAD).show();
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            downloadProgressDialoag.incrementProgressBy(1);
        }

        @Override
        protected List<Task<?>> doInBackground(Sticker... stickersList) {
                File file = new File(context.getExternalFilesDir(null), "Sticker");
                if (!file.exists()) {
                    file.mkdir();
                }
                Log.d("Message", "Download started");
                File stickerpackfolder = new File(file, stickerPack.getIdentifier());
                if (!stickerpackfolder.exists()) {
                    stickerpackfolder.mkdir();
                }
                File trayImage = new File(stickerpackfolder, stickerPack.getName() + "_trayImage.png");
                FirebaseStorage.getInstance().setMaxDownloadRetryTimeMillis(10000);
                StorageReference traydownloadRef = FirebaseStorage.getInstance().getReferenceFromUrl(stickerPack.getTrayImgUrl());
                if (!trayImage.exists()) {
                    traydownloadRef.getFile(trayImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                    tasks.add(traydownloadRef.getFile(trayImage));
                }
                stickerPack.setTrayFileName(stickerPack.getName() + "_trayImage.png");

                for (Sticker sticker : stickersList) {
                    String url = sticker.getstickerImageUrl();
                    File stickersFile = new File(stickerpackfolder, i + "_" + stickerPack.getName() + ".webp");
                    sticker.setStickerFileName(i + "_" + stickerPack.getName() + ".webp");
                    if (!stickersFile.exists()) {
                        FirebaseStorage.getInstance().setMaxDownloadRetryTimeMillis(10000);
                        StorageReference downloadRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        FileDownloadTask task = downloadRef.getFile(stickersFile);
                        task.addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                //downloadProgressDialoag.incrementProgressBy(1);
                                publishProgress(i);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                //downloadProgressDialoag.dismiss();
                                Log.d("Task","Canceled");
                            }
                        });
                        tasks.add(downloadRef.getFile(stickersFile));
                        i++;
                    } else {
                        i++;
                        publishProgress(i);
                    }
                }
            return tasks;
        }
    }
}
