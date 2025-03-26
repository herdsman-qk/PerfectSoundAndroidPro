/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.graphics.Bitmap;

class GetAction extends Action<Void> {
  GetAction(Picasso_kee picassoKee, Request data, int memoryPolicy, int networkPolicy, Object tag,
            String key) {
    super(picassoKee, null, data, memoryPolicy, networkPolicy, 0, null, key, tag, false);
  }

  @Override void complete(Bitmap result, Picasso_kee.LoadedFrom from) {
  }

  @Override public void error() {
  }
}
