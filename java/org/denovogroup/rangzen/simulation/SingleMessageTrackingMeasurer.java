package org.denovogroup.rangzen.simulation;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Bag;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class SingleMessageTrackingMeasurer implements Steppable {
  private static final long serialVersionUID = 1;

  private MessagePropagationSimulation sim;
  private Message trackedMessage;

  private int maxPropagationSeen = 0;
  private double maxTimeSeen = 0;
  private double minTimeSeen = Double.MAX_VALUE;

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
      // System.out.println("authored message"+sim.schedule.getSteps());
    }

    Bag people = sim.socialNetwork.getAllNodes();
    int seenTrackedMessageCount = 0;
    for (int i = 0; i < people.numObjs; i++) {
      Person person = (Person) people.objs[i];
      if (person.queueHasMessageWithContent(trackedMessage)) {
        seenTrackedMessageCount++;
      }
    }
    if (seenTrackedMessageCount > maxPropagationSeen && 
        time != 0) {
      timestepToPropagation.put(time, seenTrackedMessageCount);
      maxPropagationSeen = seenTrackedMessageCount;
    }
    if (time > maxTimeSeen) {
      maxTimeSeen = time;
    }
    if (time < minTimeSeen && time != 0) {
      minTimeSeen = time;
    }

    if (seenTrackedMessageCount == MessagePropagationSimulation.NUMBER_OF_PEOPLE) {
      sim.schedule.clear();
    }

    // System.out.println(String.format("%f: %d", time, seenTrackedMessageCount));
    // System.out.println(getMeasurementsAsJSON());
  }

  private void authorMessage() {
    Bag people = sim.socialNetwork.getAllNodes();
    // Random randomGenerator = new Random();
    if (people.numObjs > 0) {
      Person person = (Person) people.objs[0];
      // Person person = (Person) people.objs[randomGenerator.nextInt(people.numObjs)];
      person.addMessageToQueue(trackedMessage);
      
    }
  }

  private class OutputData {
    public Map<Double, Integer> propagationData;
    public int NUMBER_OF_PEOPLE;
    public double minTimeSeen;
    public double maxTimeSeen;
    public double NEIGHBORHOOD_RADIUS;
    public double ENCOUNTER_CHANCE;
    public double priority;
  }
  public String getMeasurementsAsJSON() {
    OutputData o = new OutputData();
    o.propagationData = timestepToPropagation;
    o.minTimeSeen = minTimeSeen;
    o.maxTimeSeen = maxTimeSeen;
    o.NEIGHBORHOOD_RADIUS = ProximityEncounterModel.NEIGHBORHOOD_RADIUS;
    o.ENCOUNTER_CHANCE = ProximityEncounterModel.ENCOUNTER_CHANCE;
    o.NUMBER_OF_PEOPLE = MessagePropagationSimulation.NUMBER_OF_PEOPLE;
    o.priority = 1;

    Gson gson = new GsonBuilder().create();
    String json = gson.toJson(o);
    // System.out.println(json);
    return json;
  }

  private void encounter(Person p1, Person p2) {
    p1.encounter(p2);
    p2.encounter(p1);
      
  }
}
