/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.graphics.Bitmap;

class FetchAction extends Action<Object> {

  private final Object target;
  private Callback callback;

  FetchAction(Picasso_kee picassoKee, Request data, int memoryPolicy, int networkPolicy, Object tag,
              String key, Callback callback) {
    super(picassoKee, null, data, memoryPolicy, networkPolicy, 0, null, key, tag, false);
    this.target = new Object();
    this.callback = callback;
  }

  @Override void complete(Bitmap result, Picasso_kee.LoadedFrom from) {
    if (callback != null) {
      callback.onSuccess();
    }
  }

  @Override void error() {
    if (callback != null) {
      callback.onError();
    }
  }

  @Override void cancel() {
    super.cancel();
    callback = null;
  }

  @Override Object getTarget() {
    return target;
  }
}
