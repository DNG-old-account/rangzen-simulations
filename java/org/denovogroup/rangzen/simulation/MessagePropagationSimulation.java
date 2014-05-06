package org.denovogroup.rangzen.simulation;

import sim.engine.Sequence;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
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

public abstract class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  public static final double METERS_PER_KILOMETER = 1000.0;
  public static final double EPSILON_TRUST = 0.001;
  public static final int MAX_FRIENDS = 40;
  public static final int NUMBER_OF_PEOPLE = 50;
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

  public MessagePropagationSimulation(long seed) {
    super(seed);
  }

  // public static void main(String[] args) {
  //   doLoop(MessagePropagationSimulation.class, args);
  //   System.exit(0);
  // }
}
