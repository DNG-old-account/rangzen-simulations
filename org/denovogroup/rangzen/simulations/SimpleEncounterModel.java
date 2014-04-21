package org.denovogroup.rangzen.simulations;

import sim.engine.Steppable;
import sim.engine.SimState;

import java.util.List;

public class SimpleEncounterModel implements Steppable {
  private static final long serialVersionUID = 1;

  private static final double encounterChance = 0.05;

  public void step(SimState state) {
    MessagePropagationSimulation sim = (MessagePropagationSimulation) state;

    List<Person> people = sim.people;
    for (Person p1 : people) {
      for (Person p2 : people) {
        if (p1 != p2 && sim.random.nextFloat() < encounterChance) {
          encounter(p1, p2);
        }
      }
    }
  }

  private void encounter(Person p1, Person p2) {
      
  }
}
