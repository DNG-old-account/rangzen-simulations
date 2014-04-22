package org.denovogroup.rangzen.simulations;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;

public class Person implements Steppable {
  private static final long serialVersionUID = 1;
  private int name;

  public Map<Person, Integer> encounterCounts = new HashMap<Person, Integer>();

  /** The message queue for the node. */
  public PriorityQueue<Message> messageQueue = new PriorityQueue<Message>();

  public Person(int name) {
    this.name = name;
    
    if (name == 0) {
      messageQueue.add(new Message("0's message!", 1));
    }
  }

  public void step(SimState state) {
    MessagePropagationSimulation sim = (MessagePropagationSimulation) state;
    takeRandomStep(sim);

    // TODO(lerner): Author message with some probability.
  }
  
  private void takeRandomStep(MessagePropagationSimulation sim) {
    Double2D me = sim.space.getObjectLocation(this);
    MutableDouble2D sumForces = new MutableDouble2D();
    sumForces.addIn(new Double2D(sim.randomMultiplier * (sim.random.nextInt(5)-2 * 1.0),
          sim.randomMultiplier * (sim.random.nextInt(5)-2 * 1.0)));

    sumForces.addIn(me);
    sim.space.setObjectLocation(this, new Double2D(sumForces));

  }

  public void putMessages(PriorityQueue<Message> newMessages, int otherName) {
    for (Message m : newMessages) {
      if (!messageQueue.contains(m)) {
        messageQueue.add(m);
        System.out.println(name+"/"+otherName+": "+messageQueue.peek());
      }
    }
  }

  public void encounter(Person other) {
    Integer count = encounterCounts.get(other);
    if (count == null) {
      encounterCounts.put(other, 1);
    }
    else {
      count++;
      encounterCounts.put(other, count);
    } 
    other.putMessages(messageQueue, name);
  }
}
