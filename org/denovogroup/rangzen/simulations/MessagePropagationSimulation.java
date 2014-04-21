package org.denovogroup.rangzen.simulations;

import sim.engine.SimState;

import java.util.List;
import java.util.ArrayList;

public class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  private static final int NUMBER_OF_PEOPLE = 100;

  /** All the people in the simulation. */
  public List<Person> people;

  public void start() {
    super.start();  // Very important!  This resets and cleans out the Schedule.
    people = new ArrayList<Person>();
    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      Person p = new Person();
      people.add(p);
    }
    schedule.scheduleRepeating(new SimpleEncounterModel());
  }

  public MessagePropagationSimulation(long seed) {
    super(seed);
  }

  public static void main(String[] args) {
    doLoop(MessagePropagationSimulation.class, args);
    System.exit(0);
  }
}
