package com.mackentoch.beaconsandroid;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.jetbrains.annotations.Nullable;

public class BeaconApplication extends Application implements BeaconMonitor {

  private static final String LOG_TAG = "BeaconsAndroidModule";

  @Override
  public void onCreate() {
    super.onCreate();
    BeaconManager.getInstanceForApplication(this).addMonitorNotifier(mMonitorNotifier);
  }

  private final MonitorNotifier mMonitorNotifier = new MonitorNotifier() {
    @Override
    public void didEnterRegion(Region region) {
      if (isAppOnBackground()) {
        BeaconApplication.this.didEnterRegion(getBundleFromRegion(region, null));
      }
    }

    @Override
    public void didExitRegion(Region region) {
      if (isAppOnBackground()) {
        BeaconApplication.this.didExitRegion(getBundleFromRegion(region, null));
      }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
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
        BeaconApplication.this.didDetermineStateForRegion(getBundleFromRegion(region, state));
      }
    }
  };

  private boolean isAppOnBackground() {
    return !ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
  }

  private Bundle getBundleFromRegion(Region region, @Nullable String state) {
    Bundle bundle = new Bundle();
    bundle.putString("identifier", region.getUniqueId());
    bundle.putString("uuid", region.getId1().toString());
    bundle.putInt("major", region.getId2() != null ? region.getId2().toInt() : 0);
    bundle.putInt("minor", region.getId3() != null ? region.getId2().toInt() : 0);
    if (state != null) {
      bundle.putString("state", state);
    }
    return bundle;
  }

  @Override
  public void didEnterRegion(Bundle bundle) {
    Log.d(LOG_TAG, "BeaconApplication - didEnterRegion");
  }

  @Override
  public void didExitRegion(Bundle bundle) {
    Log.d(LOG_TAG, "BeaconApplication - didExitRegion");
  }

  @Override
  public void didDetermineStateForRegion(Bundle bundle) {
    Log.d(LOG_TAG, "BeaconApplication - didDetermineStateForRegion");
  }
}
