package org.denovogroup.rangzen.simulation;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.field.network.Network;
import sim.util.Bag;

import au.com.bytecode.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  private static final int NUMBER_OF_PEOPLE = 1;
  public static final int width = 1000;
  public static final int height = 1000;
  public static final double discretization = 1.0;
  public static final double randomMultiplier = 0.5;

  public static final double EPSILON_TRUST = 0.001;
  public static final int MAX_FRIENDS = 40;

  /** The social network of the people in the simulation. */
  public Network socialNetwork;
  
  /** Physical space in which mobility happens. */
  public Continuous2D space;

  public void start() {
    super.start(); 

    // parselocationsfile("

    space = new Continuous2D(discretization, width, height);
    space.clear();

    // False = undirected.
    socialNetwork = new Network(false);

    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      Person p = new Person(i, Person.TRUST_POLICY_MAX_FRIENDS, this);
      // Place the person somewhere near-ish the middle of the space.
      Double2D randomLoc = new Double2D(space.getWidth() * 0.5 + random.nextInt(100) - 0.5,
          space.getHeight() * 0.5 + random.nextInt(100) - 0.5);
      space.setObjectLocation(p, randomLoc);

      try {
        p.addMobilityTrace("/Users/lerner/dropbox/code/dev/rangzen-simulations/new_abboip.txt");
      } catch (FileNotFoundException e) {
        // Well.
      }

      // See call to add social edges below. Here people are simply
      // added as entities in the network.
      socialNetwork.addNode(p);

      // Schedule the person to move, author messages, etc.
      schedule.scheduleRepeating(p);
    }

    // addRandomSocialEdges();

    // schedule.scheduleRepeating(new SimpleEncounterModel());
    schedule.scheduleRepeating(new ProximityEncounterModel());
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
    System.out.println(simLocation);
    space.setObjectLocation(object, simLocation); 
  }

  public Double2D translateLatLonToSimCoordinates(Location location) {
    double HIGHEST_LATITUDE = 37.76;
    double HIGHEST_LONGITUDE = -122.35;
    double LOWEST_LATITUDE = 37.740;
    double LOWEST_LONGITUDE = -122.45;
    
    double simX = width * (location.longitude - LOWEST_LONGITUDE)/(HIGHEST_LONGITUDE - LOWEST_LONGITUDE);
    double simY = height * (location.latitude - LOWEST_LATITUDE)/(HIGHEST_LATITUDE - LOWEST_LATITUDE);
    System.out.println(location);
    System.out.println(simX + ", " + simY);
    return new Double2D(simX, simY);
  }

  public MessagePropagationSimulation(long seed) {
    super(seed);
  }

  public static void main(String[] args) {
    doLoop(MessagePropagationSimulation.class, args);
    System.exit(0);
  }
}
