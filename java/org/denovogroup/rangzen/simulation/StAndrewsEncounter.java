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

  private Person p1;
  private Person p2;
  
  public StAndrewsEncounter(Person p1, Person p2) {
    this.p1 = p1;
    this.p2 = p2;
  }

  public void step(SimState state) {
    p1.encounter(p2);
    p2.encounter(p1);
  }

}
