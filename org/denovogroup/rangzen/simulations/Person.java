package org.denovogroup.rangzen.simulations;

import sim.engine.Steppable;
import sim.engine.SimState;

import java.util.PriorityQueue;

public class Person implements Steppable {
  private static final long serialVersionUID = 1;

  /** The message queue for the node. */
  public PriorityQueue<Message> messageQueue;

  public void step(SimState state) {
    MessagePropagationSimulation sim = (MessagePropagationSimulation) state;
    
    // TODO(lerner): Author message with some probability.
    
  }
}
