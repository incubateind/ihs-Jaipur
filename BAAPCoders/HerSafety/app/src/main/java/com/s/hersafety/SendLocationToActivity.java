package com.s.hersafety;

import android.location.Location;

class SendLocationToActivity {

    private Location mLocation;

    public SendLocationToActivity(Location mLocation) {

        this.mLocation = mLocation;
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }
}
