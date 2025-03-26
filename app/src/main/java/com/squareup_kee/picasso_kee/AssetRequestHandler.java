/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import static android.content.ContentResolver.SCHEME_FILE;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

class AssetRequestHandler extends RequestHandler {
    protected static final String ANDROID_ASSET = "android_asset";
    private static final int ASSET_PREFIX_LENGTH =
            (SCHEME_FILE + ":///" + ANDROID_ASSET + "/").length();

    private final AssetManager assetManager;

    public AssetRequestHandler(Context context) {
        assetManager = context.getAssets();
    }

    @Override
    public boolean canHandleRequest(Request data) {
        Uri uri = data.uri;
        return (SCHEME_FILE.equals(uri.getScheme())
                && !uri.getPathSegments().isEmpty() && ANDROID_ASSET.equals(uri.getPathSegments().get(0)));
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        InputStream is = assetManager.open(getFilePath(request));
        return new Result(is, Picasso_kee.LoadedFrom.DISK);
    }

    static String getFilePath(Request request) {
        return request.uri.toString().substring(ASSET_PREFIX_LENGTH);
    }
}
