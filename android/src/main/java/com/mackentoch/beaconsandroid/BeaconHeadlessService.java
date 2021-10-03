package com.mackentoch.beaconsandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

public class BeaconHeadlessService extends HeadlessJsTaskService {
  private static final long TIMEOUT_DEFAULT = 60000;
  private static final String TASK_KEY = "ReactNativeBeaconHeadlessTask";

  @Override
  protected @Nullable HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null) {
      return new HeadlessJsTaskConfig(
        TASK_KEY,
        Arguments.fromBundle(extras),
        TIMEOUT_DEFAULT,
        false
      );
    }
    return null;
  }
}
