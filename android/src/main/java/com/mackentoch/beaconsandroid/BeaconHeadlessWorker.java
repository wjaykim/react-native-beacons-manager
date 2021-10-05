package com.mackentoch.beaconsandroid;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.WorkerParameters;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

public class BeaconHeadlessWorker extends HeadlessJsTaskWorker {
  private static final long TIMEOUT_DEFAULT = 60000;
  private static final String TASK_KEY = "ReactNativeBeaconHeadlessTask";

  public BeaconHeadlessWorker(@NonNull Context context, @NonNull WorkerParameters params) {
    super(context, params);
  }

  @Nullable
  @Override
  protected HeadlessJsTaskConfig getTaskConfig(WritableMap data) {
    if (data == null) {
      return null;
    }
    return new HeadlessJsTaskConfig(
      TASK_KEY,
      data,
      TIMEOUT_DEFAULT,
      false
    );
  }
}
