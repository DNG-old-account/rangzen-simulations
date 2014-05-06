package org.denovogroup.rangzen.simulation;

import sim.engine.Steppable;
import sim.engine.SimState;
import sim.util.Bag;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class StAndrewsEncounterModel implements Serializable {
  private static final long serialVersionUID = 1;

  private static final double encounterChance = 0.0035;
  private static final int NUM_ROWS_TO_SKIP = 1;
  private static final char QUOTE_CHAR = '"';
  private static final char DELIMITER = ',';
  
  private static final int INDEX_DEVICE_1 = 0;
  private static final int INDEX_DEVICE_2 = 1;
  private static final int INDEX_ENCOUNTER_START_TIME = 2; 
  private static final int INDEX_ENCOUNTER_END_TIME = 3; 
  private static final int INDEX_ENCOUNTER_UPLOAD_TIME = 4; 
  private static final int INDEX_RSSI_VALUE = 5; 
  private static final int INDEX_ERROR_VALUE = 6; 

  private Set<StAndrewsEncounter> encounters = new HashSet<StAndrewsEncounter>();
  private StAndrewsSimulation sim;

  public StAndrewsEncounterModel(String encounterDataFilename, 
                                 StAndrewsSimulation sim) 
                                 throws FileNotFoundException {
    this.sim = sim;
    loadEncounterData(encounterDataFilename);
  }

  private void loadEncounterData(String filename) throws FileNotFoundException {
    System.err.println("Parsing St. Andrews Encounter Data File: " + filename);
    CSVReader reader;
    reader = new CSVReader(new FileReader(filename), 
                           DELIMITER, 
                           QUOTE_CHAR,
                           NUM_ROWS_TO_SKIP);
    String[] nextLine;
    try {
      while ((nextLine = reader.readNext()) != null) {
        int id1 = Integer.parseInt(nextLine[INDEX_DEVICE_1].trim());
        int id2 = Integer.parseInt(nextLine[INDEX_DEVICE_2].trim());
        double startTime = 
                Double.parseDouble(nextLine[INDEX_ENCOUNTER_START_TIME].trim());
        double endTime = 
                Double.parseDouble(nextLine[INDEX_ENCOUNTER_END_TIME].trim());
        double uploadTime = 
                Double.parseDouble(nextLine[INDEX_ENCOUNTER_UPLOAD_TIME].trim());
        double rssiValue = 
                Double.parseDouble(nextLine[INDEX_RSSI_VALUE].trim());
        // double errorValue = 
        //         Double.parseDouble(nextLine[INDEX_ERROR_VALUE].trim());
                
        Bag people = sim.socialNetwork.getAllNodes();
        Person p1 = null;
        Person p2 = null;
        for (int i=0; i<people.numObjs; i++) {
          Person p = (Person) people.get(i);
          if (p.name == id1) {
            p1 = p;
          }
          if (p.name == id2) {
            p2 = p;
          }
        }
        if (p1 != null && p2 != null) {
          double duration = endTime - startTime;
          encounters.add(new StAndrewsEncounter(p1, p2, 
                                                startTime, endTime,
                                                rssiValue));
        } else {
          throw new NullPointerException("Can't instantiate encounter with null person");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Set<StAndrewsEncounter> getEncounters() {
    return encounters;
  }

  private void encounter(Person p1, Person p2) {
    p1.encounter(p2);
    p2.encounter(p1);
      
  }
}
