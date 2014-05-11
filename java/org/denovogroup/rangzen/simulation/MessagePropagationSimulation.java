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

public class MessagePropagationSimulation extends SimState {
  private static final long serialVersionUID = 1;

  public static final int NUMBER_OF_PEOPLE = 50;
  public static final double HIGHEST_LATITUDE = 37.95;
  public static final double HIGHEST_LONGITUDE = -122.25;
  public static final double LOWEST_LATITUDE = 37.65;
  public static final double LOWEST_LONGITUDE = -122.55;

  public static final double METERS_PER_KILOMETER = 1000.0;
    
  public static final int width = 26300;
  public static final int height = 33360;
  public static final double discretization = 
          ProximityEncounterModel.NEIGHBORHOOD_RADIUS * 2;
  public static final double randomMultiplier = 0.5;
  public static final String traceIndexFilename = "data/cabdatafiles.txt";

  public static final double EPSILON_TRUST = 0.001;
  public static final int MAX_FRIENDS = 40;
  public static final double MAX_RUNTIME = 130.0; // in hours
  // Test jamming?
  public static final int NUMBER_OF_ADVERSARIES = 5;
  public static final boolean mobileJamming = true;
  public static final boolean staticJamming = false;
  public static final boolean staticJammingOptimal = false;
  public static final int NUMBER_OF_STATIC_JAMMERS = 5;
  public static final double JAMMING_RADIUS = 5000.0; // meters

  /** The encounter model in use. */
  Steppable encounterModel = new ProximityEncounterModel();

  /** The social network of the people in the simulation. */
  public Network socialNetwork;
  
  /** Physical space in which mobility happens. */
  public Continuous2D space;

  /** The agent which measures the simulation and reports statistics on it. */
  public Steppable measurer = new SingleMessageTrackingMeasurer(this);
  
  /** The location of the jammers */
  public List<Double2D> jammerLocations = new ArrayList<Double2D>();

  public void start() {
    super.start(); 

    List<String> locationTraceFilenames;
    try {
      locationTraceFilenames = getLocationTraceFilenames(traceIndexFilename); 
      // System.out.println(locationTraceFilenames);
    } catch (FileNotFoundException e) {
      System.err.println(e);
      locationTraceFilenames = new ArrayList<String>();
    }

    space = new Continuous2D(discretization, width, height);
    space.clear();

    // False = undirected.
    socialNetwork = new Network(false);
    
    Iterator<String> traceIterator = locationTraceFilenames.iterator();

    schedule.scheduleOnce(measurer);     
    
    
    // Set up static jammers
    if (staticJamming) {
               
        //strategic locations (From the simulated annleaing approach)
        if (JAMMING_RADIUS == 100 && staticJammingOptimal) {
            jammerLocations.add(new Double2D(37.7857, -122.4173));
            jammerLocations.add(new Double2D(37.7916, -122.4080));
            jammerLocations.add(new Double2D(37.7911, -122.4129));
            jammerLocations.add(new Double2D(37.7874, -122.4106));
            jammerLocations.add(new Double2D(37.7971, -122.4106));
        } else if (JAMMING_RADIUS == 200 && staticJammingOptimal) {
            jammerLocations.add(new Double2D(37.7964, -122.4056));
            jammerLocations.add(new Double2D(37.7867, -122.4097));
            jammerLocations.add(new Double2D(37.7989, -122.4088));
            jammerLocations.add(new Double2D(37.7860, -122.4052));
            jammerLocations.add(new Double2D(37.7906, -122.4095));
        }else if (JAMMING_RADIUS == 500 && staticJammingOptimal) {
            jammerLocations.add(new Double2D(37.7964, -122.4338));
            jammerLocations.add(new Double2D(37.7874, -122.4189));
            jammerLocations.add(new Double2D(37.7910, -122.3991));
            jammerLocations.add(new Double2D(37.7865, -122.4072));
            jammerLocations.add(new Double2D(37.7959, -122.4077));
        }else if (JAMMING_RADIUS == 700 && staticJammingOptimal) {
            jammerLocations.add(new Double2D(37.7887, -122.3968));
            jammerLocations.add(new Double2D(37.7912, -122.4195));
            jammerLocations.add(new Double2D(37.7836, -122.4076));
            jammerLocations.add(new Double2D(37.7798, -122.4183));
            jammerLocations.add(new Double2D(37.7950, -122.4076));
            // jammerLocations.add(new Double2D(37.7912, -122.4239));
            // jammerLocations.add(new Double2D(37.7817, -122.4006));
            // jammerLocations.add(new Double2D(37.8019, -122.4107));
            // jammerLocations.add(new Double2D(37.7931, -122.4025));
            // jammerLocations.add(new Double2D(37.7868, -122.4120));
        }else if (JAMMING_RADIUS == 1000 && staticJammingOptimal) {
            jammerLocations.add(new Double2D(37.7937, -122.4280));
            jammerLocations.add(new Double2D(37.7566, -122.4000));
            jammerLocations.add(new Double2D(37.7819, -122.4136));
            jammerLocations.add(new Double2D(37.8000, -122.4099));
            jammerLocations.add(new Double2D(37.7883, -122.3964));
        }else if (JAMMING_RADIUS == 1300 && staticJammingOptimal) {
            jammerLocations.add(new Double2D(37.7971, -122.4312));
            jammerLocations.add(new Double2D(37.7594, -122.4029));
            jammerLocations.add(new Double2D(37.7888, -122.4065));
            jammerLocations.add(new Double2D(37.7735, -122.4253));
            jammerLocations.add(new Double2D(37.8135, -122.4135));
        }else if (JAMMING_RADIUS == 1500 && staticJammingOptimal) {            
            jammerLocations.add(new Double2D(37.7790, -122.4210));
            jammerLocations.add(new Double2D(37.7586, -122.4007));
            jammerLocations.add(new Double2D( 37.7912, -122.3966));
            jammerLocations.add(new Double2D(37.7966, -122.4428));
            jammerLocations.add(new Double2D(37.8088, -122.4170));
        }else if (JAMMING_RADIUS == 2000 && staticJammingOptimal) {            
            jammerLocations.add(new Double2D(37.7955, -122.4136));
            jammerLocations.add(new Double2D(37.7755, -122.4445));
            jammerLocations.add(new Double2D(37.6682, -122.3973));
            jammerLocations.add(new Double2D(37.7591, -122.4100));
            jammerLocations.add(new Double2D(37.7773, -122.3773));
        }else if (JAMMING_RADIUS == 5000 && staticJammingOptimal) {            
            jammerLocations.add(new Double2D(37.7469, -122.4715));
            jammerLocations.add(new Double2D(37.7285, -122.3654));
            jammerLocations.add(new Double2D(37.8300, -122.2915));
            jammerLocations.add(new Double2D(37.8069, -122.4069));
            jammerLocations.add(new Double2D(37.6685, -122.4254));
        } else {
            //random locations
            for (int i=0; i<NUMBER_OF_STATIC_JAMMERS; i++) {
                Double2D randomLoc = new Double2D(space.getWidth() * 0.5 + random.nextInt(100) - 0.5,
                                                space.getHeight() * 0.5 + random.nextInt(100) - 0.5);
                jammerLocations.add(randomLoc);
            }
        }
    }
    
    // Create the people as either adversaries or regular citizens
    Person p;
    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      // System.err.println("Here:" + i + " " + arrayContains(adversaries,i));
      
      //--------- Add people to the network-----------
      p = new Person(i, Person.TRUST_POLICY_SIGMOID_FRACTION_OF_FRIENDS, this);
      
      
      // Place the person somewhere near-ish the middle of the space.
      // Double2D randomLoc = new Double2D(space.getWidth() * 0.5 + random.nextInt(100) - 0.5,
      //     space.getHeight() * 0.5 + random.nextInt(100) - 0.5);
      // space.setObjectLocation(p, randomLoc);

      try {
        p.addMobilityTrace(traceIterator.next());
        setObjectLatLonLocation(p, p.mobilityTrace.locations.get(0));
      } catch (FileNotFoundException e) {
        System.err.println(e);
        // Well.
      }
      // System.err.println("Person: " + ((Person)p).trustPolicy);

      // See call to add social edges below. Here people are simply
      // added as entities in the network.
      
      socialNetwork.addNode(p);

      // Schedule the person to move, author messages, etc.
      // schedule.scheduleRepeating(p);
      p.schedule();
    }
    System.err.println("Adding scale-free social graph.");
    addScaleFreeRandomSocialGraph();
    
    // Throw in some adversaries at the lowest-degree nodes
    createAdversaries();
    // schedule.scheduleRepeating(new SimpleEncounterModel());
    // schedule.scheduleOnce(new ProximityEncounterModel());

  }
  
  public boolean arrayContains(int[] ar, int value) {
    for (int i = 0; i<ar.length; i++) {
        if (ar[i] == value){
            return true;
        }
    }
    return false;
  }
  
  public boolean bagContains(Bag bag, Object obj) {
    for (Object item : bag) {
        if (item == obj){
            return true;
        }
    }
    return false;
  }

  public void finish() {
    // String filename = "data.json";
    String jsonOutput = ((SingleMessageTrackingMeasurer) measurer).getMeasurementsAsJSON();
    System.out.println(jsonOutput);
    // try {
    //   File outfile = new File(filename);
    //   if (!outfile.exists()) {
    //     outfile.createNewFile();
    //   }
    //   FileWriter fw = new FileWriter(outfile.getAbsoluteFile());
    //   BufferedWriter bw  = new BufferedWriter(fw);
    //   bw.write(jsonOutput);
    //   bw.close(); 
    //   System.err.println("Outputted run data to " + filename);
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }
  }

  private void addRandomSocialEdges() {
    Bag people = socialNetwork.getAllNodes();
    for (Object person : people) {
      for (int i=0; i<5; i++) {
      // Choose a random second person.
      Object personB = null;
      do {
        personB = people.get(random.nextInt(people.numObjs));
      } while (person == personB);

      double buddiness = 1.0;
      socialNetwork.addEdge(person, personB, new Double(buddiness));
      // System.out.println(person + " is friends with " + personB);
      }
    }
  }
  
  private void addScaleFreeRandomSocialGraph() {
    /** Implements the Barabasi-Albert model for building a social graph */
    Bag people = socialNetwork.getAllNodes();
    
    Bag friends = new Bag();
    double probability;
    int totalDegree = 0;
    double attractiveness;
    boolean adversaryFlag = false;
    for (Object person : people) {      
      for (Object otherPerson : people) {
        if (person == otherPerson) {
            continue;
        }
        // Draw an edge according to Barabasi-Albert model
        probability = random.nextDouble();
        // how much a node is likely to attract a new node
        socialNetwork.getEdges(person,friends);
        attractiveness = ((double)friends.numObjs)/totalDegree + 0.02;
        if ((probability < attractiveness) || (totalDegree == 0)){
        
          double buddiness = 1.0;
          socialNetwork.addEdge(person, otherPerson, new Double(buddiness));
          totalDegree = totalDegree + 1;
          // System.out.println(person + " is friends with " + personB);
        }
      }
    }
  }
  
  public void createAdversaries(){
    // --------Assign adversaries to the worst-connected nodes--------------
    Bag people = socialNetwork.getAllNodes();
    
    // Get the ordered list of nodes in increasing degree
    List<Integer> indices = orderNodesByDegree(people);
    
    // Now assign the lowest-connected nodes to adversaries
    int numAdversaries = 0;
    Bag allAdversaryFriends = new Bag();
    Bag allAdversaries = new Bag();
    Bag myFriends = new Bag();
    while ( numAdversaries < NUMBER_OF_ADVERSARIES) {
        // find which node has the cnt lowest degree
        int authorIdx = indices.get(numAdversaries);
        
        // assign adversaries to lowest-degree nodes
        Person person = (Person) people.objs[authorIdx];
            
        // Make the person an adversary
        
        person.trustPolicy = Person.TRUST_POLICY_ADVERSARY;
        allAdversaries.add(person);
        
        numAdversaries++;
                    
        // Add this person's friends to the adversarial Bag-o-friends
        socialNetwork.getEdges(person,myFriends);
        for ( Object friend : myFriends ) {
            Object otherNode = ((Edge) friend).getOtherNode(person);
            if (! bagContains(allAdversaryFriends, otherNode )) {
                allAdversaryFriends.add(otherNode);
            }
        }
    }
    
    
    // Make sure each adversary has the ENTIRE adversarial Bag-o-friends
    double buddiness = 1.0;
    for (Object adv : allAdversaries) {
        for (Object friend : allAdversaryFriends ) {
            socialNetwork.getEdges(adv,myFriends);
            if (! bagContains(myFriends,friend)) {
                socialNetwork.addEdge(adv, friend, new Double(buddiness));
            }
        }
    }
    
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

  public void setObjectLatLonLocation(Object object, Location location) {
    Double2D simLocation = translateLatLonToSimCoordinates(location);
    // System.out.println(simLocation);
    space.setObjectLocation(object, simLocation); 
  }

  /**
   * Transforms a location (with latitude/longitude coordinates) into
   * a place on the simulation's continuous 2D space. The coordinates
   * of the simulation's space are in meters.
   *
   * @param location A Location object to be transformed into simulation
   * space.
   * @return A Double2D with simulation coordinates corresponding to the
   * given Location, or null if location is null.
   */
  public Double2D translateLatLonToSimCoordinates(Location location) {
    if (location == null) {
      return null;
    }

    double simX;
    double simY;

    LatLng origin = new LatLng(LOWEST_LATITUDE, LOWEST_LONGITUDE);
    LatLng cornerA = new LatLng(LOWEST_LATITUDE, location.longitude);
    LatLng cornerB = new LatLng(location.latitude, LOWEST_LONGITUDE);

    simX = origin.distance(cornerA) * METERS_PER_KILOMETER;
    simY = height - origin.distance(cornerB) * METERS_PER_KILOMETER;

    // Old method assumed non-spherical earth.
    // double simX = width * (location.longitude - LOWEST_LONGITUDE)/(HIGHEST_LONGITUDE - LOWEST_LONGITUDE);
    // double simY = height * (location.latitude - LOWEST_LATITUDE)/(HIGHEST_LATITUDE - LOWEST_LATITUDE);
    // System.out.println(location);
    // System.out.println(simX + ", " + simY);
    return new Double2D(simX, simY);
  }

  private List<String> getLocationTraceFilenames(String traceIndexFilename) throws FileNotFoundException {
    List<String> locationTraceFilenames = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new FileReader(traceIndexFilename));
    String line;
    try {
      while ((line = br.readLine()) != null) {
        locationTraceFilenames.add(line);      
      }
      br.close();
    } catch (IOException e) {
      System.err.println(e);
    }
    return locationTraceFilenames;
  }

  public void schedulePerson(Person person, double time) {
    Steppable[] steps = new Steppable[3];
    steps[0] = person;
    steps[1] = measurer;
    steps[2] = encounterModel;
    Sequence sequence = new Sequence(steps);
    schedule.scheduleOnce(time, sequence);
  }

  public MessagePropagationSimulation(long seed) {
    super(seed);
  }

  public static void main(String[] args) {
    doLoop(MessagePropagationSimulation.class, args);
    System.exit(0);
  }
}
