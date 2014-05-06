package org.denovogroup.rangzen.simulation;

import sim.engine.Sequence;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;

import au.com.bytecode.opencsv.CSVReader;

import uk.me.jstott.jcoord.LatLng;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class ProximitySimulation extends MessagePropagationSimulation {
  private static final long serialVersionUID = 1;

  public static final double HIGHEST_LATITUDE = 37.95;
  public static final double HIGHEST_LONGITUDE = -122.25;
  public static final double LOWEST_LATITUDE = 37.65;
  public static final double LOWEST_LONGITUDE = -122.55;

  public static final int width = 26300;
  public static final int height = 33360;
  public static final double discretization = 
          ProximityEncounterModel.NEIGHBORHOOD_RADIUS * 2;
  public static final String traceIndexFilename = "data/cabdatafiles.txt";

  /** The agent which measures the simulation and reports statistics on it. */
  public Steppable measurer = new SingleMessageTrackingMeasurer(this);

  public void start() {
    super.start(); 

    List<String> locationTraceFilenames;
    try {
      locationTraceFilenames = getLocationTraceFilenames(traceIndexFilename); 
      // System.out.println(locationTraceFilenames);
    } catch (FileNotFoundException e) {
      System.err.println(e);
      locationTraceFilenames = new ArrayList<String>();
    }

    space = new Continuous2D(discretization, width, height);
    space.clear();

    // False = undirected.
    socialNetwork = new Network(false);

    Iterator<String> traceIterator = locationTraceFilenames.iterator();

    schedule.scheduleOnce(measurer);     

    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      Person p = new Person(i, Person.TRUST_POLICY_SIGMOID_FRACTION_OF_FRIENDS, this);

      try {
        p.addMobilityTrace(traceIterator.next());
        setObjectLatLonLocation(p, p.mobilityTrace.locations.get(0));
      } catch (FileNotFoundException e) {
        System.err.println(e);
        // Well.
      }

      // See call to add social edges below. Here people are simply
      // added as entities in the network.
      socialNetwork.addNode(p);

      // Schedule the person to move, author messages, etc.
      // schedule.scheduleRepeating(p);
      p.schedule();

    }

    addRandomSocialEdges();
  }

  public void finish() {
    String jsonOutput = ((SingleMessageTrackingMeasurer) measurer).getMeasurementsAsJSON();
    System.out.println(jsonOutput);
  }

  private void addRandomSocialEdges() {
    Bag people = socialNetwork.getAllNodes();
    for (Object person : people) {
      for (int i=0; i<5; i++) {
      // Choose a random second person.
      Object personB = null;
      do {
        personB = people.get(random.nextInt(people.numObjs));
      } while (person == personB);

      double buddiness = 1.0;
      socialNetwork.addEdge(person, personB, new Double(buddiness));
      // System.out.println(person + " is friends with " + personB);
      }
    }
  }

  public void setObjectLatLonLocation(Object object, Location location) {
    Double2D simLocation = translateLatLonToSimCoordinates(location);
    // System.out.println(simLocation);
    space.setObjectLocation(object, simLocation); 
  }

  /**
   * Transforms a location (with latitude/longitude coordinates) into
   * a place on the simulation's continuous 2D space. The coordinates
   * of the simulation's space are in meters.
   *
   * @param location A Location object to be transformed into simulation
   * space.
   * @return A Double2D with simulation coordinates corresponding to the
   * given Location, or null if location is null.
   */
  public Double2D translateLatLonToSimCoordinates(Location location) {
    if (location == null) {
      return null;
    }

    double simX;
    double simY;

    LatLng origin = new LatLng(LOWEST_LATITUDE, LOWEST_LONGITUDE);
    LatLng cornerA = new LatLng(LOWEST_LATITUDE, location.longitude);
    LatLng cornerB = new LatLng(location.latitude, LOWEST_LONGITUDE);

    simX = origin.distance(cornerA) * METERS_PER_KILOMETER;
    simY = height - origin.distance(cornerB) * METERS_PER_KILOMETER;

    return new Double2D(simX, simY);
  }

  private List<String> getLocationTraceFilenames(String traceIndexFilename) throws FileNotFoundException {
    List<String> locationTraceFilenames = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new FileReader(traceIndexFilename));
    String line;
    try {
      while ((line = br.readLine()) != null) {
        locationTraceFilenames.add(line);      
      }
      br.close();
    } catch (IOException e) {
      System.err.println(e);
    }
    return locationTraceFilenames;
  }

  public void schedulePerson(Person person, double time) {
    Steppable[] steps = new Steppable[3];
    steps[0] = person;
    steps[1] = measurer;
    steps[2] = encounterModel;
    Sequence sequence = new Sequence(steps);
    schedule.scheduleOnce(time, sequence);
  }

  public ProximitySimulation(long seed) {
    super(seed);
  }

  public static void main(String[] args) {
    doLoop(ProximitySimulation.class, args);
    System.exit(0);
  }
}
