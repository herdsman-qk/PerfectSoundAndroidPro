/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import static com.squareup_kee.picasso_kee.Picasso_kee.LoadedFrom;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Represents an arbitrary listener for image loading.
 * <p>
 * Objects implementing this class <strong>must</strong> have a working implementation of
 * {@link Object#equals(Object)} and {@link Object#hashCode()} for proper storage internally.
 * Instances of this interface will also be compared to determine if view recycling is occurring.
 * It is recommended that you add this interface directly on to a custom view type when using in an
 * adapter to ensure correct recycling behavior.
 */
public interface Target {
  /**
   * Callback when an image has been successfully loaded.
   * <p>
   * <strong>Note:</strong> You must not recycle the bitmap.
   */
  void onBitmapLoaded(Bitmap bitmap, LoadedFrom from);

  /**
   * Callback indicating the image could not be successfully loaded.
   * <p>
   * <strong>Note:</strong> The passed {@link Drawable} may be {@code null} if none has been
   * specified via {@link RequestCreator#error(Drawable)}
   * or {@link RequestCreator#error(int)}.
   */
  void onBitmapFailed(Drawable errorDrawable);

  /**
   * Callback invoked right before your request is submitted.
   * <p>
   * <strong>Note:</strong> The passed {@link Drawable} may be {@code null} if none has been
   * specified via {@link RequestCreator#placeholder(Drawable)}
   * or {@link RequestCreator#placeholder(int)}.
   */
  void onPrepareLoad(Drawable placeHolderDrawable);
}
