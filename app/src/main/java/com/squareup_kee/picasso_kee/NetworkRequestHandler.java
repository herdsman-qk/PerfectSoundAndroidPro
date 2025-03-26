/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import static com.squareup_kee.picasso_kee.Downloader.Response;

import android.graphics.Bitmap;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;

class NetworkRequestHandler extends RequestHandler {
  static final int RETRY_COUNT = 2;

  private static final String SCHEME_HTTP = "http";
  private static final String SCHEME_HTTPS = "https";

  private final Downloader downloader;
  private final Stats stats;

  public NetworkRequestHandler(Downloader downloader, Stats stats) {
    this.downloader = downloader;
    this.stats = stats;
  }

  @Override public boolean canHandleRequest(Request data) {
    String scheme = data.uri.getScheme();
    return (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme));
  }

  @Override public Result load(Request request, int networkPolicy) throws IOException {
    Response response = downloader.load(request.uri, request.networkPolicy);
    if (response == null) {
      return null;
    }

    Picasso_kee.LoadedFrom loadedFrom = response.cached ? Picasso_kee.LoadedFrom.DISK : Picasso_kee.LoadedFrom.NETWORK;

    Bitmap bitmap = response.getBitmap();
    if (bitmap != null) {
      return new Result(bitmap, loadedFrom);
    }

    InputStream is = response.getInputStream();
    if (is == null) {
      return null;
    }
    // Sometimes response content length is zero when requests are being replayed. Haven't found
    // root cause to this but retrying the request seems safe to do so.
    if (loadedFrom == Picasso_kee.LoadedFrom.DISK && response.getContentLength() == 0) {
      Utils.closeQuietly(is);
      throw new ContentLengthException("Received response with 0 content-length header.");
    }
    if (loadedFrom == Picasso_kee.LoadedFrom.NETWORK && response.getContentLength() > 0) {
      stats.dispatchDownloadFinished(response.getContentLength());
    }
    return new Result(is, loadedFrom);
  }

  @Override int getRetryCount() {
    return RETRY_COUNT;
  }

  @Override boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
    return info == null || info.isConnected();
  }

  @Override boolean supportsReplay() {
    return true;
  }

  static class ContentLengthException extends IOException {
    public ContentLengthException(String message) {
      super(message);
    }
  }
}
