package org.denovogroup.rangzen.simulation;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Bag;

// import com.google.gson;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class SingleMessageTrackingMeasurer implements Steppable {
  private static final long serialVersionUID = 1;

  private MessagePropagationSimulation sim;
  private Message trackedMessage;

  // Storages the history of message propgation.
  Map<Double, Integer> timestepToPropagation;

  public SingleMessageTrackingMeasurer(MessagePropagationSimulation sim) {
    this.sim = sim;
    this.trackedMessage = new Message(UUID.randomUUID().toString(), 1.0);
    this.timestepToPropagation = new HashMap<Double, Integer>();
  }

  public void step(SimState state) {
    MessagePropagationSimulation sim = (MessagePropagationSimulation) state;
    double time = sim.schedule.getTime();

    if (sim.schedule.getSteps() == 0) {
      authorMessage();
      System.out.println("authored message"+sim.schedule.getSteps());
    }

    Bag people = sim.socialNetwork.getAllNodes();
    int seenTrackedMessageCount = 0;
    for (int i = 0; i < people.numObjs; i++) {
      Person person = (Person) people.objs[i];
      if (person.queueHasMessageWithContent(trackedMessage)) {
        seenTrackedMessageCount++;
      }
    }
    timestepToPropagation.put(time, seenTrackedMessageCount);

    System.out.println(String.format("%f: %d", time, seenTrackedMessageCount));
  }

  private void authorMessage() {
    Bag people = sim.socialNetwork.getAllNodes();
    if (people.numObjs > 0) {
      Person person = (Person) people.objs[0];
      person.messageQueue.add(trackedMessage);
    }
  }

  private void encounter(Person p1, Person p2) {
    p1.encounter(p2);
    p2.encounter(p1);
      
  }
}
