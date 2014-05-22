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
import java.util.Arrays;
import java.util.TreeMap;

import java.lang.Math;


public class SingleMessageTrackingMeasurer implements Steppable {
  private static final long serialVersionUID = 1;

  private MessagePropagationSimulation sim;
  private Message trackedMessage;

  private static final double MILLISECONDS_PER_SECOND = 1000.0;
  private static final double SECONDS_PER_MINUTE = 60.0;
  private static final double MINUTES_PER_HOUR = 60.0;
  private static final double HOURS_PER_DAY = 24.0;


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
    String author;
    boolean popularAuthor;
    
    if (sim.schedule.getSteps() == 0) {
      /** Compose a message */
      try { 
        author = ((ProximitySimulation)sim).messageAuthor;
        popularAuthor = ((ProximitySimulation)sim).popularAuthor;
      } catch (ClassCastException e) {
        author = ((StAndrewsSimulation)sim).messageAuthor;
        popularAuthor = ((StAndrewsSimulation)sim).popularAuthor;
      }
        
      if (author == ProximitySimulation.POPULAR_AUTHOR) {
        if (popularAuthor) {
            System.err.println("Message from a popular person.");
        } else {
            System.err.println("Message from an unpopular person.");
        }
        authorMessagePopular(popularAuthor);//Authors a message from a popular or unpopular node
      } else if (author == ProximitySimulation.ADVERSARIAL_AUTHOR) {
        authorMessageAdversarial(); // author a message from an adversary
        System.err.println("Message from an adversary.");
      } else {
        System.err.println("Message from an average person.");
        authorMessage(); //Uses a random node in the network as an author
      }
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

    // Stop running if the simulation has been going too long
    double hours = (time - minTimeSeen) / 1000 / 60 / 60;
    if (seenTrackedMessageCount == ProximitySimulation.NUMBER_OF_PEOPLE || (hours > ProximitySimulation.MAX_RUNTIME && ProximitySimulation.MAX_RUNTIME > 0) ) {
      sim.schedule.clear();
    }

    // System.out.println(String.format("%f: %d", time, seenTrackedMessageCount));
    // System.out.println(getMeasurementsAsJSON());
  }

  private void authorMessage() {
    Bag people = sim.socialNetwork.getAllNodes();
    Bag tmp = new Bag();
    if (people.numObjs > 0) {
      // Person person = (Person) people.objs[0];
      Person person = (Person) people.objs[sim.random.nextInt(people.numObjs)];
      person.addMessageToQueue(trackedMessage);
      System.err.println("degree of the author is "+sim.socialNetwork.getEdges(person, tmp).numObjs);
      
    }
  }
  
  private void authorMessageAdversarial() {
    Bag people = sim.socialNetwork.getAllNodes();
    // Random randomGenerator = new Random();
    for (Object p : people) {
        Person person = (Person) p;
        if (person.trustPolicy == Person.TRUST_POLICY_ADVERSARY) { 
            person.addMessageToQueue(trackedMessage);
            return;
        }      
    }
  }
  
  private void authorMessagePopular(boolean popularFlag) {
    // if popularFlag == true, start the message from a popular node
    // else, start it from an unpopular node
    int author; 
    Bag people = sim.socialNetwork.getAllNodes();
    
    if (people.numObjs == 0) { 
        return;
    }
    
    // rank the social graph by degree
    List<Integer> indices = sim.orderNodesByDegree(people);
    
    //Choose an (un)popular node at random among the (bottom) top 'boundary' degrees
    author = 1; // either 2 or 48
    if (popularFlag) {
        author = people.numObjs - author;
    } else { 
        author = Math.max(author,((ProximitySimulation)sim).NUMBER_OF_ADVERSARIES);
    }
    int authorIdx = indices.get(author);

    // Retrieve the random 'author' element of the nodes, sorted by degree
    // This is pretty inefficient :( Must be a better way to do it
    // ArrayList<Map.Entry<Double,Object>> list = new ArrayList<Map.Entry<Double,Object>>(sorted_map.entrySet());
    // Map.Entry<Double,Object> pair = list.get(author);
    // Person person = (Person) pair.getValue();
    people = sim.socialNetwork.getAllNodes();
    Person person = (Person) people.objs[0];
    person = (Person) people.objs[authorIdx];
    // System.err.println("Degree = " + (sim.socialNetwork.getEdges(person).numObjs) + "and author is " + author);
        
    // Add the message to the queue
    person.addMessageToQueue(trackedMessage);
    Bag tmp = new Bag();
    System.err.println("degree of the author is "+sim.socialNetwork.getEdges(person, tmp).numObjs);
  }

  private class OutputData {
    public Map<Double, Integer> propagationData;
    public int NUMBER_OF_PEOPLE;
    public double minTimeSeen;
    public double maxTimeSeen;
    public double NEIGHBORHOOD_RADIUS;
    public double ENCOUNTER_CHANCE;
    public double NUMBER_OF_ADVERSARIES;
    public double priority;
    public double duration;
    public double minutesDuration;
    public double hoursDuration;
    public double daysDuration;
  }
  public String getMeasurementsAsJSON() {
    OutputData o = new OutputData();
    o.propagationData = timestepToPropagation;
    o.minTimeSeen = minTimeSeen;
    o.maxTimeSeen = maxTimeSeen;
    o.NEIGHBORHOOD_RADIUS = ProximityEncounterModel.NEIGHBORHOOD_RADIUS;
    o.ENCOUNTER_CHANCE = ProximityEncounterModel.ENCOUNTER_CHANCE;
    o.NUMBER_OF_ADVERSARIES = ProximitySimulation.NUMBER_OF_ADVERSARIES;
    o.NUMBER_OF_PEOPLE = ProximitySimulation.NUMBER_OF_PEOPLE;
    // Guessing that the time is in milliseconds instead of seconds
    if (maxTimeSeen > 1399679023 * 100) {
      o.duration = (maxTimeSeen - minTimeSeen) / MILLISECONDS_PER_SECOND;
      o.minutesDuration = o.duration/SECONDS_PER_MINUTE;
      o.hoursDuration = o.minutesDuration/MINUTES_PER_HOUR;
      o.daysDuration = o.hoursDuration/HOURS_PER_DAY;
    } else {
      o.duration = maxTimeSeen - minTimeSeen;
      o.minutesDuration = o.duration/SECONDS_PER_MINUTE;
      o.hoursDuration = o.minutesDuration/MINUTES_PER_HOUR;
      o.daysDuration = o.hoursDuration/HOURS_PER_DAY;
    }
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
