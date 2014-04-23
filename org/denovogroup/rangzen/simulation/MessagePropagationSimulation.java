package org.denovogroup.rangzen.simulation;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.field.network.Network;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.List;

public class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  private static final int NUMBER_OF_PEOPLE = 100;
  public static final int width = 1000;
  public static final int height = 1000;
  public static final double discretization = 1.0;
  public static final double randomMultiplier = 0.5;

  /** The social network of the people in the simulation. */
  public Network socialNetwork;
  
  /** Physical space in which mobility happens. */
  public Continuous2D space;

  public void start() {
    super.start(); 

    space = new Continuous2D(discretization, width, height);
    space.clear();

    // False = undirected.
    socialNetwork = new Network(false);

    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      Person p = new Person(i);
      // Place the person somewhere near-ish the middle of the space.
      Double2D randomLoc = new Double2D(space.getWidth() * 0.5 + random.nextInt(100) - 0.5,
          space.getHeight() * 0.5 + random.nextInt(100) - 0.5);
      space.setObjectLocation(p, randomLoc);

      // See call to add social edges below. Here people are simply
      // added as entities in the network.
      socialNetwork.addNode(p);

      // Schedule the person to move, author messages, etc.
      schedule.scheduleRepeating(p);
    }

    addRandomSocialEdges();

    // schedule.scheduleRepeating(new SimpleEncounterModel());
    schedule.scheduleRepeating(new ProximityEncounterModel());
  }

  private void addRandomSocialEdges() {
    Bag people = socialNetwork.getAllNodes();
    for (Object person : people) {

      // Choose a random second person
      Object personB = null;
      do {
        personB = people.get(random.nextInt(people.numObjs));
      } while (person == personB);

      double buddiness = 1.0;
      socialNetwork.addEdge(person, personB, new Double(buddiness));
    }
  }

  public MessagePropagationSimulation(long seed) {
    super(seed);
  }

  public static void main(String[] args) {
    doLoop(MessagePropagationSimulation.class, args);
    System.exit(0);
  }
}
