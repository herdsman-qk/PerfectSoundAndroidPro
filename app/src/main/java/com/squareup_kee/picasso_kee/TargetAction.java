/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

final class TargetAction extends Action<Target> {

  TargetAction(Picasso_kee picassoKee, Target target, Request data, int memoryPolicy, int networkPolicy,
               Drawable errorDrawable, String key, Object tag, int errorResId) {
    super(picassoKee, target, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key, tag,
        false);
  }

  @Override void complete(Bitmap result, Picasso_kee.LoadedFrom from) {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }
    Target target = getTarget();
    if (target != null) {
      target.onBitmapLoaded(result, from);
      if (result.isRecycled()) {
        throw new IllegalStateException("Target callback must not recycle bitmap!");
      }
    }
  }

  @Override void error() {
    Target target = getTarget();
    if (target != null) {
      if (errorResId != 0) {
        target.onBitmapFailed(picassoKee.context.getResources().getDrawable(errorResId));
      } else {
        target.onBitmapFailed(errorDrawable);
      }
    }
  }
}
