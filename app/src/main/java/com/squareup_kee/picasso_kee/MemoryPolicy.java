/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

/** Designates the policy to use when dealing with memory cache. */
@SuppressWarnings("PointlessBitwiseExpression")
public enum MemoryPolicy {

  /** Skips memory cache lookup when processing a request. */
  NO_CACHE(1 << 0),
  /**
   * Skips storing the final result into memory cache. Useful for one-off requests
   * to avoid evicting other bitmaps from the cache.
   */
  NO_STORE(1 << 1);

  static boolean shouldReadFromMemoryCache(int memoryPolicy) {
    return (memoryPolicy & MemoryPolicy.NO_CACHE.index) == 0;
  }

  static boolean shouldWriteToMemoryCache(int memoryPolicy) {
    return (memoryPolicy & MemoryPolicy.NO_STORE.index) == 0;
  }

  final int index;

  private MemoryPolicy(int index) {
    this.index = index;
  }
}
