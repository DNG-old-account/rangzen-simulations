package org.denovogroup.rangzen.simulation;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Bag;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StAndrewsEncounter implements Steppable {
  private static final long serialVersionUID = 1;

  public Person p1;
  public Person p2;
  public double startTime;
  public double endTime;
  public double duration;
  public double rssi;
  
  public StAndrewsEncounter(Person p1, Person p2, 
                            double startTime, double endTime,
                            double rssi) {
    this.p1 = p1;
    this.p2 = p2;
    this.startTime = startTime;
    this.endTime = endTime;
    this.rssi = rssi;

    this.duration = endTime - startTime;
  }

  public void step(SimState state) {
    p1.encounter(p2);
    p2.encounter(p1);
  }

}
