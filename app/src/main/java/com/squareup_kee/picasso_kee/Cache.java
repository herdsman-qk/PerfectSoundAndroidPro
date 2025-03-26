/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.graphics.Bitmap;

/**
 * A memory cache for storing the most recently used images.
 * <p>
 * <em>Note:</em> The {@link Cache} is accessed by multiple threads. You must ensure
 * your {@link Cache} implementation is thread safe when {@link Cache#get(String)} or {@link
 * Cache#set(String, Bitmap)} is called.
 */
public interface Cache {
  /** Retrieve an image for the specified {@code key} or {@code null}. */
  Bitmap get(String key);

  /** Store an image in the cache for the specified {@code key}. */
  void set(String key, Bitmap bitmap);

  /** Returns the current size of the cache in bytes. */
  int size();

  /** Returns the maximum size in bytes that the cache can hold. */
  int maxSize();

  /** Clears the cache. */
  void clear();

  /** Remove items whose key is prefixed with {@code keyPrefix}. */
  void clearKeyUri(String keyPrefix);

  /** A cache which does not store any values. */
  Cache NONE = new Cache() {
    @Override public Bitmap get(String key) {
      return null;
    }

    @Override public void set(String key, Bitmap bitmap) {
      // Ignore.
    }

    @Override public int size() {
      return 0;
    }

    @Override public int maxSize() {
      return 0;
    }

    @Override public void clear() {
    }

    @Override public void clearKeyUri(String keyPrefix) {
    }
  };
}
