/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

/** Designates the policy to use for network requests. */
@SuppressWarnings("PointlessBitwiseExpression")
public enum NetworkPolicy {

  /** Skips checking the disk cache and forces loading through the network. */
  NO_CACHE(1 << 0),

  /**
   * Skips storing the result into the disk cache.
   * <p>
   * <em>Note</em>: At this time this is only supported if you are using OkHttp.
   */
  NO_STORE(1 << 1),

  /** Forces the request through the disk cache only, skipping network. */
  OFFLINE(1 << 2);

  public static boolean shouldReadFromDiskCache(int networkPolicy) {
    return (networkPolicy & NetworkPolicy.NO_CACHE.index) == 0;
  }

  public static boolean shouldWriteToDiskCache(int networkPolicy) {
    return (networkPolicy & NetworkPolicy.NO_STORE.index) == 0;
  }

  public static boolean isOfflineOnly(int networkPolicy) {
    return (networkPolicy & NetworkPolicy.OFFLINE.index) != 0;
  }

  final int index;

  private NetworkPolicy(int index) {
    this.index = index;
  }
}
