package com.mackentoch.beaconsandroid;

import android.os.Bundle;

public interface BeaconMonitor {
  void didEnterRegion(Bundle bundle);

  void didExitRegion(Bundle bundle);

  void didDetermineStateForRegion(Bundle bundle);
}
