/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.graphics.Bitmap;

/** Image transformation. */
public interface Transformation {
  /**
   * Transform the source bitmap into a new bitmap. If you create a new bitmap instance, you must
   * call {@link Bitmap#recycle()} on {@code source}. You may return the original
   * if no transformation is required.
   */
  Bitmap transform(Bitmap source);

  /**
   * Returns a unique key for the transformation, used for caching purposes. If the transformation
   * has parameters (e.g. size, scale factor, etc) then these should be part of the key.
   */
  String key();
}
