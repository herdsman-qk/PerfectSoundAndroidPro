/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class KeEIDBRequestHandler extends RequestHandler {
    private static final int DB_PREFIX_LENGTH =
            ("idb:///").length();

    public KeEIDBRequestHandler() {
    }

    @Override
    public boolean canHandleRequest(Request data) {
        Uri uri = data.uri;
        return "idb".equals(uri.getScheme());
    }

    private static Method loadFileMethod = null;

    public static void setLoadFileMethod(Method m) {
        loadFileMethod = m;
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        if (loadFileMethod == null) {
            return new Result(new ByteArrayInputStream(null), Picasso_kee.LoadedFrom.DISK);
        } else {
            InputStream bais = null;
            try {
                bais = (InputStream) loadFileMethod.invoke(null, getFilePath(request));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return new Result(bais, Picasso_kee.LoadedFrom.DISK);
        }
    }

    static String getFilePath(Request request) {
        return request.uri.toString().substring(DB_PREFIX_LENGTH);
    }
}
