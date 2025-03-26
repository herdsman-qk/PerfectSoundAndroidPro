/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import static android.content.ContentResolver.SCHEME_ANDROID_RESOURCE;
import static com.squareup_kee.picasso_kee.Picasso_kee.LoadedFrom.DISK;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

class ResourceRequestHandler extends RequestHandler {
  private final Context context;

  ResourceRequestHandler(Context context) {
    this.context = context;
  }

  @Override public boolean canHandleRequest(Request data) {
    if (data.resourceId != 0) {
      return true;
    }

    return SCHEME_ANDROID_RESOURCE.equals(data.uri.getScheme());
  }

  @Override public Result load(Request request, int networkPolicy) throws IOException {
    Resources res = Utils.getResources(context, request);
    int id = Utils.getResourceId(res, request);
    return new Result(decodeResource(res, id, request), DISK);
  }

  private static Bitmap decodeResource(Resources resources, int id, Request data) {
    final BitmapFactory.Options options = createBitmapOptions(data);
    if (requiresInSampleSize(options)) {
      BitmapFactory.decodeResource(resources, id, options);
      calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
    }
    return BitmapFactory.decodeResource(resources, id, options);
  }
}
