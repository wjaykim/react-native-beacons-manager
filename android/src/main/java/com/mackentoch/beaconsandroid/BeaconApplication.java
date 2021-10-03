package com.mackentoch.beaconsandroid;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.jetbrains.annotations.Nullable;

public class BeaconApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    BeaconManager.getInstanceForApplication(this).addMonitorNotifier(mMonitorNotifier);
  }

  private final MonitorNotifier mMonitorNotifier = new MonitorNotifier() {
    @Override
    public void didEnterRegion(Region region) {
    }

    @Override
    public void didExitRegion(Region region) {
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
        Intent service = new Intent(getApplicationContext(), BeaconHeadlessService.class);
        service.putExtras(getBundleFromRegion(region, state));
        getApplicationContext().startService(service);
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
}
