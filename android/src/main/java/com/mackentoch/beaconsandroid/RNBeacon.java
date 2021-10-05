package com.mackentoch.beaconsandroid;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.jetbrains.annotations.Nullable;

public class RNBeacon {

  public static void init(Context context) {
    new RNBeacon(context);
  }

  public RNBeacon(Context context) {
    MonitorNotifier mMonitorNotifier = new MonitorNotifier() {
      @Override
      public void didEnterRegion(Region region) {
      }

      @Override
      public void didExitRegion(Region region) {
      }

      @Override
      public void didDetermineStateForRegion(int i, Region region) {
        Log.d("sibal", region.getId3() + " " + i);
        if (isAppOnBackground()) {
          String state = "unknown";
          switch (i) {
            case MonitorNotifier.INSIDE:
              state = "inside";
              break;
            case MonitorNotifier.OUTSIDE:
              state = "outside";
              break;
            default:
              break;
          }
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
    };
    BeaconManager.getInstanceForApplication(context).addMonitorNotifier(mMonitorNotifier);
  }

  private boolean isAppOnBackground() {
    return !ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
  }

  private Data getDataFromRegion(Region region, @Nullable String state) {
    Data.Builder builder = new Data.Builder()
      .putString("identifier", region.getUniqueId())
      .putString("uuid", region.getId1().toString())
      .putInt("major", region.getId2() != null ? region.getId2().toInt() : 0)
      .putInt("minor", region.getId3() != null ? region.getId2().toInt() : 0)
      .putString("state", state);
    return builder.build();
  }
}
