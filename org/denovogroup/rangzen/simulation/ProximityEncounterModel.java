package org.denovogroup.rangzen.simulation;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.Bag;

import java.util.List;

public class ProximityEncounterModel implements Steppable {
  private static final long serialVersionUID = 1;

  private static final double NEIGHBORHOOD_RADIUS = 5.0;
  private static final double encounterChance = 0.2;


  public void step(SimState state) {
    MessagePropagationSimulation sim = (MessagePropagationSimulation) state;

    List<Person> people = sim.people;
    for (Person p1 : people) {
      Double2D location = sim.space.getObjectLocation(p1);
      Bag neighborhood = 
            sim.space.getNeighborsExactlyWithinDistance(location, 
                                                        NEIGHBORHOOD_RADIUS);
      for (Object p2 : neighborhood) {
        p1.encounter((Person) p2);
      }
    }
    System.out.println("Done stepping encounter model");

  }

  private void encounter(Person p1, Person p2) {
    p1.encounter(p2);
    p2.encounter(p1);
      
  }
}
