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

public class MobilityTrace implements Iterable<Location> {

  private List<Location> locations;

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
    System.out.println("Parsing " + filename);
    char DELIMITER = ' ';
    CSVReader reader;
    reader = new CSVReader(new FileReader(filename), DELIMITER);
    String [] nextLine;
    try {
      while ((nextLine = reader.readNext()) != null) {
        // nextLine[] is an array of values from the line
        System.out.println(nextLine[0] + ", " + nextLine[1]);
      }
    } catch (IOException e) {
      return null;
    }
    return null;

  }
}
