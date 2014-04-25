package org.denovogroup.rangzen.simulation;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.field.network.Network;
import sim.util.Bag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Location {
  public double latitude;
  public double longitude;
  public Date date;

  public Location(double latitude, double longitude, Date date) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.date = date;
  }

  public Location(double latitude, double longitude, long date) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.date = new Date(date);
  }

  public String toString() {
    return String.format("(%f, %f) @%s", latitude, longitude, date.toString());
  }
}
