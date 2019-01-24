package com.android.mp.wastickermaster;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WriteToJSON {
    public Context context;
    public StickerPack stickerPackClass;
    boolean flag = false;

    public WriteToJSON(StickerPack stickerPackClass, Context context) {
        this.stickerPackClass = stickerPackClass;
        this.context = context;
    }

    public static void writeJsonFile(File file, JSONObject jsonObject) {
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(String.valueOf(jsonObject));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void makeJsonFile() throws JSONException, IOException {
        File JSONFile = new File(context.getExternalFilesDir(null) + "/stickers.json");
        if (!JSONFile.exists()) {
            JSONFile.createNewFile();
        }
        writeJSON(StickerObject(), StickerPackObject(), JSONFile);
        Log.d("File", "JSON file sucessfully created");
    }

    public void writeJSON(JSONObject jsonObject, JSONObject stickerPackObject, File file) throws IOException, JSONException {
        if (file.length() == 0) {
            writeJsonFile(file, jsonObject);
        } else {
            String lastObject = getStringFronFile(new FileInputStream(file));
            try {
                JSONObject prevJSONObj = new JSONObject(lastObject);
                JSONArray array = prevJSONObj.getJSONArray("sticker_packs");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.getString("identifier").equals(stickerPackClass.identifier)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    prevJSONObj.accumulate("sticker_packs", stickerPackObject);
                    writeJsonFile(file, prevJSONObj);
                }
            } catch (Exception e) {
                Log.d("Json Error", e.toString());
            }
        }
    }

    public String getStringFronFile(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public JSONObject StickerObject() throws JSONException {
        JSONObject mainStickerObject = new JSONObject();
        try {
            mainStickerObject.put("android_play_store_link", "http://play.google.com/store/apps/details?id=com.android.mp.wastickermaster");
            JSONArray array = new JSONArray();
            array.put(StickerPackObject());
            mainStickerObject.put("sticker_packs", array);
        } catch (Exception e) {
            Log.d("Json Error", e.toString());
        }
        return mainStickerObject;
    }

    public JSONObject StickerPackObject() {
        JSONObject stickerPack = new JSONObject();
        try {
            stickerPack.put("identifier", stickerPackClass.getIdentifier());
            stickerPack.put("name", stickerPackClass.getName());
            stickerPack.put("publisher", stickerPackClass.getPublisher());
            stickerPack.put("tray_image_file", stickerPackClass.getTrayFileName());
            stickerPack.put("publisher_email", "");
            stickerPack.put("publisher_website", "");
            stickerPack.put("privacy_policy_website", "");
            stickerPack.put("license_agreement_website", "");

            JSONArray stickersArr = new JSONArray();
            for (Sticker sticker : stickerPackClass.getStickers()) {
                JSONObject stickers = new JSONObject();
                stickers.put("image_file", sticker.getStickerFileName());
                JSONArray emojisArr = new JSONArray();
                emojisArr.put("");
                emojisArr.put("");
                stickers.put("emojis", emojisArr);
                stickersArr.put(stickers);
            }
            stickerPack.put("stickers", stickersArr);
        } catch (Exception e) {
            Log.e("Json Error", e.toString());
        }
        return stickerPack;
    }
}
