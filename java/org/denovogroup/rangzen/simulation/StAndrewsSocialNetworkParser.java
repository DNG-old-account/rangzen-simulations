package org.denovogroup.rangzen.simulation;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StAndrewsSocialNetworkParser implements Serializable {

  private Map<Integer, Person> people = new HashMap<Integer, Person>();
  private Network network = new Network(UNDIRECTED);
  private MessagePropagationSimulation sim;

  public static final boolean UNDIRECTED = false;
  public static final int INDEX_ID1 = 0;
  public static final int INDEX_ID2 = 1;

  public static final String TRUST_POLICY = 
          Person.TRUST_POLICY_SIGMOID_FRACTION_OF_FRIENDS;
  public static final int NUM_ROWS_TO_SKIP = 1;
  public static final char QUOTE_CHAR = '"';

  public StAndrewsSocialNetworkParser(Network network, MessagePropagationSimulation sim) {
    this.network = network;
    this.sim = sim;
  }
  
  public StAndrewsSocialNetworkParser(String filename, 
                                      MessagePropagationSimulation sim) 
                                  throws FileNotFoundException {
    this.sim = sim;
    parseNetworkFile(filename);
  }

  private void parseNetworkFile(String filename) throws FileNotFoundException {
    System.err.println("Parsing " + filename);
    char DELIMITER = ',';
    CSVReader reader;
    reader = new CSVReader(new FileReader(filename), 
                           DELIMITER, 
                           QUOTE_CHAR,
                           NUM_ROWS_TO_SKIP);
    String [] nextLine;
    try {
      while ((nextLine = reader.readNext()) != null) {
        int id1 = Integer.parseInt(nextLine[INDEX_ID1].trim());
        int id2 = Integer.parseInt(nextLine[INDEX_ID2].trim());

        Person p1;
        Person p2;
        if (!people.containsKey(id1)) {
          p1 = new Person(id1, TRUST_POLICY, sim);
          people.put(id1, p1);
          network.addNode(p1);
        } else {
          p1 = people.get(id1);
        }
        if (!people.containsKey(id2)) {
          p2 = new Person(id2, TRUST_POLICY, sim);
          people.put(id2, p2);
          network.addNode(p2);
        } else {
          p2 = people.get(id2);
        }
        network.addEdge(p1, p2, new Double(1.0));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
  public Network getNetwork() {
    return network;
  }
}
