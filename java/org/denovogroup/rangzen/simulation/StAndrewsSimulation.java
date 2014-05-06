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

public class StAndrewsSimulation extends MessagePropagationSimulation {
  private static final long serialVersionUID = 1;

  /** The agent which measures the simulation and reports statistics on it. */
  public Steppable measurer = new SingleMessageTrackingMeasurer(this);

  public static final String ST_ANDREWS_SOCIAL_NETWORK_FILENAME = 
          "data/standrews/srsn.csv";
  public static final String ST_ANDREWS_ENCOUNTER_FILENAME = 
          "data/standrews/dsn.csv";

  private StAndrewsSocialNetworkParser parser;
  private StAndrewsEncounterModel saEncounterModel;

  public void start() {
    super.start(); 
    try {
      parser = new StAndrewsSocialNetworkParser(ST_ANDREWS_SOCIAL_NETWORK_FILENAME, 
                                               this);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    socialNetwork = parser.getNetwork();
    try {
      saEncounterModel = new StAndrewsEncounterModel(ST_ANDREWS_ENCOUNTER_FILENAME,
                                                   this);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    // ((StAndrewsEncounterModel) encounterModel).schedule();

    schedule.scheduleOnce(measurer);     
  }

  public void finish() {
    String jsonOutput = ((SingleMessageTrackingMeasurer) measurer).getMeasurementsAsJSON();
    System.out.println(jsonOutput);
  }

  public StAndrewsSimulation(long seed) {
    super(seed);
  }

  public static void main(String[] args) {
    doLoop(StAndrewsSimulation.class, args);
    System.exit(0);
  }
}
