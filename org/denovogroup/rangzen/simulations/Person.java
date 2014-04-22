package org.denovogroup.rangzen.simulations;

import sim.engine.Steppable;
import sim.engine.SimState;

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
    
    // TODO(lerner): Author message with some probability.
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
