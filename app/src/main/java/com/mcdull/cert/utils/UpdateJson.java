package com.mcdull.cert.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;

import java.io.FileOutputStream;

/**
 * Created by Begin on 16/8/4.
 */
public class UpdateJson {
    public static void update(final Context context) {
        AVQuery<AVObject> avQuery = new AVQuery<>("Json");
        avQuery.getInBackground("57a205e379bc4400549f5f3a", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    final int jsonVersion = avObject.getInt("jsonVersion");
                    final int urlVersion = avObject.getInt("urlVersion");
                    final SharedPreferences sp = context.getSharedPreferences("version", Context.MODE_PRIVATE);
                    if (jsonVersion > sp.getInt("jsonVersion", 0)) {
                        AVFile json = avObject.getAVFile("url");
                        json.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                if (e == null) {
                                    try {
                                        FileOutputStream fileOutputStream = context.openFileOutput("url", Context.MODE_PRIVATE);
                                        fileOutputStream.write(bytes);
                                        fileOutputStream.close();
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putInt("urlVersion", urlVersion);
                                        edit.commit();
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                    if (urlVersion > sp.getInt("urlVersion", 0)) {
                        AVFile url = avObject.getAVFile("json");
                        url.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                if (e == null) {
                                    try {
                                        FileOutputStream fileOutputStream = context.openFileOutput("json.jar", Context.MODE_PRIVATE);
                                        fileOutputStream.write(bytes);
                                        fileOutputStream.close();
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putInt("jsonVersion", jsonVersion);
                                        edit.commit();
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void beUpdate(final Context context) {
        AVQuery<AVObject> avQuery = new AVQuery<>("Json");
        avQuery.getInBackground("57a205e379bc4400549f5f3a", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    final int jsonVersion = avObject.getInt("jsonVersion");
                    final int urlVersion = avObject.getInt("urlVersion");
                    final SharedPreferences sp = context.getSharedPreferences("version", Context.MODE_PRIVATE);
                    AVFile json = avObject.getAVFile("url");
                    json.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            if (e == null) {
                                try {
                                    FileOutputStream fileOutputStream = context.openFileOutput("url", Context.MODE_PRIVATE);
                                    fileOutputStream.write(bytes);
                                    fileOutputStream.close();
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putInt("urlVersion", urlVersion);
                                    edit.commit();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    AVFile url = avObject.getAVFile("json");
                    url.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            if (e == null) {
                                try {
                                    FileOutputStream fileOutputStream = context.openFileOutput("json.jar", Context.MODE_PRIVATE);
                                    fileOutputStream.write(bytes);
                                    fileOutputStream.close();
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putInt("jsonVersion", jsonVersion);
                                    edit.commit();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

}
