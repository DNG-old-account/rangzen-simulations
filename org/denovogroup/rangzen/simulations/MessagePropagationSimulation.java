package org.denovogroup.rangzen.simulations;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

import java.util.List;
import java.util.ArrayList;

public class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  private static final int NUMBER_OF_PEOPLE = 100;
  public static final int width = 1000;
  public static final int height = 1000;
  public static final double discretization = 1.0;

  public Continuous2D space;

  /** All the people in the simulation. */
  public List<Person> people;

  public void start() {
    super.start();  // Very important!  This resets and cleans out the Schedule.

    // Discretization is confusing (
    space = new Continuous2D(discretization, width, height);
    space.clear();

    people = new ArrayList<Person>();
    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      Person p = new Person(i);
      people.add(p);

      Double2D randomLoc = new Double2D(space.getWidth() * 0.5 + random.nextInt(100) - 0.5,
          space.getHeight() * 0.5 + random.nextInt(100) - 0.5);
      space.setObjectLocation(p, randomLoc);
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
