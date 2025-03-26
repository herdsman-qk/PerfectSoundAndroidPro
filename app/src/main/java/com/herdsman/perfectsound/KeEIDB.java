/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */

package com.herdsman.perfectsound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup_kee.picasso_kee.KeEIDBRequestHandler;
import com.squareup_kee.picasso_kee.Picasso_kee;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class KeEIDB {
    private static final String TAG = "KeEIDB";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    public static boolean OK = false;

    public static boolean setup(Context context) {
        mContext = context;
        try {
            Method m = KeEIDB.class.getMethod("inputStream", String.class);
            KeEIDBRequestHandler.setLoadFileMethod(m);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        OK = true;
        return OK;
    }

    public static void putBitmap(Context context, ImageView target, String path) {
        Picasso_kee.with(context)
                .load(Uri.parse("idb:///" + path))
                .fit()
                .into(target);
    }

    public static Bitmap bitmap(String path) {
        try {
            return BitmapFactory.decodeStream(inputStream(path));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Drawable drawableByXml(String path) {
        try {
            Log.d(TAG, "drawableByXml: " + "db/" + path);
            XmlResourceParser xmlResourceParser = mContext.getAssets().openXmlResourceParser("db/" + path);
            return Drawable.createFromXml(mContext.getResources(), xmlResourceParser);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable drawable(String path) {
        try {
            return Drawable.createFromStream(inputStream(path), null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    public static InputStream inputStream(String path) {
        try {
            return mContext.getAssets().open("db/" + path);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static String[] fileList(String path) {
        try {
            String[] tmp = mContext.getAssets().list("db/" + path);
            ArrayList<String> arr = new ArrayList<>();
            for (String s : tmp) {
                if (s.contains(".")) {
                    arr.add(path + "/" + s);
                }
            }
            return arr.toArray(new String[]{});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] dirList(String path) {
        try {
            String[] tmp = mContext.getAssets().list(path);
            ArrayList<String> arr = new ArrayList<>();
            for (String s : tmp) {
                if (!s.contains(".")) {
                    arr.add(path + "/" + s);
                }
            }
            return arr.toArray(new String[]{});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
