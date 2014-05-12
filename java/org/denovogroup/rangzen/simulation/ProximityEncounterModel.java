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
        // Check for a jammer in the jamming radius
        if (sim.mobileJamming) {
            boolean jammed = false;
            Bag neighborhood = 
                sim.space.getNeighborsExactlyWithinDistance(location, sim.JAMMING_RADIUS);
                for (Object p2 : neighborhood) {
                    // System.err.println("trust policy is "+((Person) p2).trustPolicy);
                    if (((Person) p2).trustPolicy == Person.TRUST_POLICY_ADVERSARY) {
                        jammed = true;
                        break;
                    }
                }
            if (jammed) {
                continue;
            }
        } else if (sim.staticJamming) {
            boolean jammed = false;
            // compute distance to all the jammers
            for (int j=0; j<sim.jammerLocations.size(); j++) {
                // System.err.println("Distance from jammer is " + location.distance((Double2D) sim.jammerLocations.get(j)));
                if (location.distance((Double2D) sim.jammerLocations.get(j)) < sim.JAMMING_RADIUS ) {
                    jammed = true;
                    // System.err.println("found a jammer");
                    break;
                }
            }
            if (jammed) {
                continue;
            }
        }
        Bag neighborhood = 
            sim.space.getNeighborsExactlyWithinDistance(location, 
                                                        NEIGHBORHOOD_RADIUS);
      
        for (Object p2 : neighborhood) {
            if (sim.random.nextDouble() < ENCOUNTER_CHANCE && p1 != p2) {
              // System.err.println(((Person)p1).trustPolicy + " and " + ((Person)p2).trustPolicy);
              ((Person) p1).encounter((Person) p2);
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
