package org.denovogroup.rangzen.simulation;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.field.network.Network;
import sim.util.Bag;

import au.com.bytecode.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;

public class MobilityTrace implements Iterable<Location>, Serializable {

  public List<Location> locations;
  public static final int INDEX_LATITUDE = 0;
  public static final int INDEX_LONGITUDE = 1;
  public static final int INDEX_DATE = 3;

  public MobilityTrace(List<Location> locations) {
    this.locations = locations;
  }
  
  public MobilityTrace(String filename) throws FileNotFoundException {
    this.locations = parseLocationsFile(filename);
  }

  public Iterator<Location> iterator() {
    return locations.iterator();
  }

  private List<Location> parseLocationsFile(String filename) throws FileNotFoundException {
    System.err.println("Parsing " + filename);
    char DELIMITER = ' ';
    CSVReader reader;
    reader = new CSVReader(new FileReader(filename), DELIMITER);
    String [] nextLine;
    List<Location> locations = new ArrayList<Location>();
    try {
      while ((nextLine = reader.readNext()) != null) {
        double lat = Double.parseDouble(nextLine[INDEX_LATITUDE]);
        double lon = Double.parseDouble(nextLine[INDEX_LONGITUDE]);
        long date = Long.parseLong(nextLine[INDEX_DATE]) * 1000;
        Location location = new Location(lat, lon, date);
        locations.add(location);
      }
    } catch (IOException e) {
      return null;
    }
    return locations;

  }
}
