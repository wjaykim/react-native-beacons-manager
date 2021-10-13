package com.mackentoch.beaconsandroid;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class RNBeacon {

  public static void init(Context context) {
    new RNBeacon(context);
  }

  public RNBeacon(Context context) {
    MonitorNotifier mMonitorNotifier = new MonitorNotifier() {
      @Override
      public void didEnterRegion(Region region) {
        enqueueTask(context, region, "enter");
      }

      @Override
      public void didExitRegion(Region region) {
        enqueueTask(context, region, "exit");
      }

      @Override
      public void didDetermineStateForRegion(int i, Region region) {
      }
    };
    BeaconManager.getInstanceForApplication(context).addMonitorNotifier(mMonitorNotifier);
  }

  private boolean isAppOnBackground() {
    return !ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
  }

  private void enqueueTask(Context context, Region region, String state) {
    if (isAppOnBackground()) {
      Data inputData = getDataFromRegion(region, state);
      WorkRequest headlessJsTaskWorkRequest =
        new OneTimeWorkRequest.Builder(BeaconHeadlessWorker.class)
          .setInputData(inputData)
          .build();
      WorkManager
        .getInstance(context)
        .enqueue(headlessJsTaskWorkRequest);
    }
  }

  private Data getDataFromRegion(Region region, String event) {
    Data.Builder builder = new Data.Builder()
      .putString("identifier", region.getUniqueId())
      .putString("uuid", region.getId1().toString())
      .putInt("major", region.getId2() != null ? region.getId2().toInt() : 0)
      .putInt("minor", region.getId3() != null ? region.getId2().toInt() : 0)
      .putString("event", event);
    return builder.build();
  }
}
