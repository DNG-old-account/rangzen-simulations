package org.denovogroup.rangzen.simulation;

import sim.engine.Sequence;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.field.network.Edge;
import sim.util.Bag;
import sim.util.Double2D;

import au.com.bytecode.opencsv.CSVReader;

import uk.me.jstott.jcoord.LatLng;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import java.util.TreeMap;

public abstract class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  public static final double METERS_PER_KILOMETER = 1000.0;
  public static final double EPSILON_TRUST = 4.001;
  public static final int MAX_FRIENDS = 40;
  public static final int NUMBER_OF_PEOPLE = 50;
  public static final double MAX_RUNTIME = 150.0; // in hours
  public static final double randomMultiplier = 0.5;
    
  /** Physical space in which mobility happens. */
  public Continuous2D space;

  /** The encounter model in use. */
  Steppable encounterModel = new ProximityEncounterModel();

  /** The social network of the people in the simulation. */
  public Network socialNetwork;

  public void start() {
    super.start(); 
  }

  public List<Integer> orderNodesByDegree(Bag people) {
    /** takes in a bag of people in the social network, returns a list of integers with the ordering from
    smallest to largest */
    Bag friends = new Bag();
    int[] degreeArray = new int[people.numObjs];
    TreeMap<Double,Object> sorted_map = new TreeMap<Double,Object>();
        
    // characterize the degree distribution
    int count = 0;
    for (Object person : people) {
        socialNetwork.getEdges(person,friends);
        degreeArray[count] = friends.numObjs;
        count += 1;
    }
    
    // Sort and store the indices
    TreeMap<Integer, List<Integer>> map = new TreeMap<Integer, List<Integer>>();
    for(int i = 0; i < degreeArray.length; i++) {
        List<Integer> ind = map.get(degreeArray[i]);
        if(ind == null){
            ind = new ArrayList<Integer>();
            map.put(degreeArray[i], ind);
        }
        ind.add(i);
    }

    // Now flatten the list
    List<Integer> indices = new ArrayList<Integer>();
    for(List<Integer> arr : map.values()) {
        indices.addAll(arr);
    }
    return indices;
  }
  public MessagePropagationSimulation(long seed) {
    super(seed);
  }

  // public static void main(String[] args) {
  //   doLoop(MessagePropagationSimulation.class, args);
  //   System.exit(0);
  // }
}
