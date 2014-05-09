package org.denovogroup.rangzen.simulation;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.Bag;

import java.util.List;

public class ProximityEncounterModel implements Steppable {
  private static final long serialVersionUID = 1;

  public static final double NEIGHBORHOOD_RADIUS = 20;
  public static final double ENCOUNTER_CHANCE = 0.05;

  public void step(SimState state) {
    ProximitySimulation sim = (ProximitySimulation) state;

    Bag people = sim.socialNetwork.getAllNodes();
    for (Object p1 : people) {
      Double2D location = sim.space.getObjectLocation(p1);
      if (location != null) {
        Bag neighborhood = 
          sim.space.getNeighborsExactlyWithinDistance(location, 
              NEIGHBORHOOD_RADIUS);
        for (Object p2 : neighborhood) {
          if (sim.random.nextDouble() < ENCOUNTER_CHANCE && p1 != p2) {
            ((Person) p1).encounter((Person) p2);
          }
        }
      }
    }
    // System.out.println("Done stepping encounter model");

  }

  private void encounter(Person p1, Person p2) {
    p1.encounter(p2);
    p2.encounter(p1);
      
  }
}
