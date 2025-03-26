/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

public interface Callback {
  void onSuccess();

  void onError();

  public static class EmptyCallback implements Callback {

    @Override public void onSuccess() {
    }

    @Override public void onError() {
    }
  }
}
