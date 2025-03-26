/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

abstract class RemoteViewsAction extends Action<RemoteViewsAction.RemoteViewsTarget> {
  final RemoteViews remoteViews;
  final int viewId;

  private RemoteViewsTarget target;

  RemoteViewsAction(Picasso_kee picassoKee, Request data, RemoteViews remoteViews, int viewId,
                    int errorResId, int memoryPolicy, int networkPolicy, Object tag, String key) {
    super(picassoKee, null, data, memoryPolicy, networkPolicy, errorResId, null, key, tag, false);
    this.remoteViews = remoteViews;
    this.viewId = viewId;
  }

  @Override void complete(Bitmap result, Picasso_kee.LoadedFrom from) {
    remoteViews.setImageViewBitmap(viewId, result);
    update();
  }

  @Override public void error() {
    if (errorResId != 0) {
      setImageResource(errorResId);
    }
  }

  @Override RemoteViewsTarget getTarget() {
    if (target == null) {
      target = new RemoteViewsTarget(remoteViews, viewId);
    }
    return target;
  }

  void setImageResource(int resId) {
    remoteViews.setImageViewResource(viewId, resId);
    update();
  }

  abstract void update();

  static class RemoteViewsTarget {
    final RemoteViews remoteViews;
    final int viewId;

    RemoteViewsTarget(RemoteViews remoteViews, int viewId) {
      this.remoteViews = remoteViews;
      this.viewId = viewId;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RemoteViewsTarget remoteViewsTarget = (RemoteViewsTarget) o;
      return viewId == remoteViewsTarget.viewId && remoteViews.equals(
          remoteViewsTarget.remoteViews);
    }

    @Override public int hashCode() {
      return 31 * remoteViews.hashCode() + viewId;
    }
  }

  static class AppWidgetAction extends RemoteViewsAction {
    private final int[] appWidgetIds;

    AppWidgetAction(Picasso_kee picassoKee, Request data, RemoteViews remoteViews, int viewId,
                    int[] appWidgetIds, int memoryPolicy, int networkPolicy, String key, Object tag,
                    int errorResId) {
      super(picassoKee, data, remoteViews, viewId, errorResId, memoryPolicy, networkPolicy, tag, key);
      this.appWidgetIds = appWidgetIds;
    }

    @Override void update() {
      AppWidgetManager manager = AppWidgetManager.getInstance(picassoKee.context);
      manager.updateAppWidget(appWidgetIds, remoteViews);
    }
  }

  static class NotificationAction extends RemoteViewsAction {
    private final int notificationId;
    private final Notification notification;

    NotificationAction(Picasso_kee picassoKee, Request data, RemoteViews remoteViews, int viewId,
                       int notificationId, Notification notification, int memoryPolicy, int networkPolicy,
                       String key, Object tag, int errorResId) {
      super(picassoKee, data, remoteViews, viewId, errorResId, memoryPolicy, networkPolicy, tag, key);
      this.notificationId = notificationId;
      this.notification = notification;
    }

    @Override void update() {
      NotificationManager manager = Utils.getService(picassoKee.context, NOTIFICATION_SERVICE);
      manager.notify(notificationId, notification);
    }
  }
}
