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
import java.util.Date;
import java.util.TreeMap;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.UnrecognizedOptionException;

public class ProximitySimulation extends MessagePropagationSimulation {
  private static final long serialVersionUID = 1;

  public static final double HIGHEST_LATITUDE = 37.95;
  public static final double HIGHEST_LONGITUDE = -122.25;
  public static final double LOWEST_LATITUDE = 37.65;
  public static final double LOWEST_LONGITUDE = -122.55;

  public static final int width = 26300;
  public static final int height = 33360;
  public static final double discretization = 
          ProximityEncounterModel.NEIGHBORHOOD_RADIUS * 2;
          
  // Population parameters        
  public static int NUMBER_OF_PEOPLE = 0;
  public static int NUMBER_OF_ADVERSARIES = 0;
  
  // -------Simulation parameters------//
  public static final double EPSILON_TRUST = .001;
  public static final int MAX_FRIENDS = 40;
  public static final double MAX_RUNTIME = 150; // in hours
  
  // Jamming
  public static boolean mobileJamming = false;
  public static boolean staticJamming = false;
  public static boolean staticJammingOptimal = false;
  public static int NUMBER_OF_STATIC_JAMMERS = 0;
  public static int NUMBER_OF_MOBILE_JAMMERS = 0;
  public static double JAMMING_RADIUS = 50.0; // meters  
   
  
  // Message authorship
  public static final String RANDOM_AUTHOR = "Random author";
  public static final String ADVERSARIAL_AUTHOR = "Adversarial author";
  public static final String POPULAR_AUTHOR = "(Un)popular author";
  
  public static String messageAuthor = RANDOM_AUTHOR;
  public static boolean popularAuthor = false;

 // Mobility trace
  public static final String CABSPOTTING_MOBILITY_TRACE_INDEX_FILE =
          "data/cabdatafiles.txt";
  public static final String CABSPOTTING_OPTIMAL_JAMMER_LOCATIONS =
          "data/cabspottingdata/jammerLocations/";

  public static final char QUOTE_CHAR = '"';
  
  public static final String GOWALLA_SOCIAL_NETWORK_FILE = 
          "data/gowalla/loc-gowalla_edges.txt";
  public static final String GOWALLA_MOBILITY_TRACE_FILE = 
          "data/gowalla/loc-gowalla_totalCheckins.txt";
  // public static final String GOWALLA_SOCIAL_NETWORK_FILE = 
  //         "data/gowalla/firstHundredEdges.txt";
  // public static final String GOWALLA_MOBILITY_TRACE_FILE = 
  //         "data/gowalla/firstHundredCheckins.txt";
  public static final int GOWALLA_MIN_PERSON_ID = 0;
  public static final char GOWALLA_DELIMITER = '\t';
  public static final int GOWALLA_INDEX_PERSON_ID = 0;
  public static final int GOWALLA_INDEX_DATE = 1;
  public static final int GOWALLA_INDEX_LATITUDE = 2;
  public static final int GOWALLA_INDEX_LONGITUDE = 3;
  public static final int GOWALLA_INDEX_LOCATION_ID = 4;
  public static final int GOWALLA_LINES_TO_SKIP = 1;
  // Restrict gowalla to London
  public static final double GOWALLA_MIN_LATITUDE = 51.1091401;
  public static final double GOWALLA_MAX_LATITUDE = 51.6728598;
  public static final double GOWALLA_MIN_LONGITUDE = -0.5950405999999475;
  public static final double GOWALLA_MAX_LONGITUDE = 0.30717490000006364;
  // Restrict gowalla to Los Angeles
  // public static final double GOWALLA_MIN_LATITUDE = 33.7700504;
  // public static final double GOWALLA_MAX_LATITUDE = 34.1808392;
  // public static final double GOWALLA_MIN_LONGITUDE = -117.91450359999999;
  // public static final double GOWALLA_MAX_LONGITUDE = -118.4911912;
  

  private String traceIndexFilename = CABSPOTTING_MOBILITY_TRACE_INDEX_FILE; //GOWALLA_MOBILITY_TRACE_FILE; //CABSPOTTING_MOBILITY_TRACE_INDEX_FILE;

  /** The agent which measures the simulation and reports statistics on it. */
  public Steppable measurer = new SingleMessageTrackingMeasurer(this);

  public List<Double2D> jammerLocations = new ArrayList<Double2D>();

  public void start() {
    super.start(); 


    space = new Continuous2D(discretization, width, height);
    space.clear();

    // False = undirected.
    socialNetwork = new Network(false);

    schedule.scheduleOnce(measurer);     

    addCabspottingPeopleAndRandomSocialNetwork(); 
    // addGowallaPeopleAndSocialNetwork();
    
    // Throw in some adversaries at the lowest-degree nodes
    createAdversaries();
    
    
    addJammers(); 
    

    System.err.println("Start() complete. All input files parsed.");
    
  }

  public void finish() {
    String jsonOutput = ((SingleMessageTrackingMeasurer) measurer).getMeasurementsAsJSON();
    System.out.println(jsonOutput);
  }
  
  private void addJammers() {
    /** This method adds stationary jammers to the grid in either optimally-chosen or random locations */ 
    
    // Set up static jammers
    if (staticJamming) {
               
        //strategic locations (From the simulated annleaing approach)
        if (staticJammingOptimal) {
            try {
                String csvFile = CABSPOTTING_OPTIMAL_JAMMER_LOCATIONS + "cabspotting_"+((int)JAMMING_RADIUS)+".csv";
                placeOptimalJammers(csvFile);
            }
            catch (Exception e) {
                placeJammersRandomly();
            }
        }
        else {
            //random locations
            placeJammersRandomly();
        }
    }
    
    // set up mobile jammers (as members of the adversarial team)
    if (mobileJamming) {
        placeMobileJammers();
    }

  }
  
  private void placeJammersRandomly() {
    //place the jammers at random in the grid
    for (int i=0; i<NUMBER_OF_STATIC_JAMMERS; i++) {
        Double2D randomLoc = new Double2D(space.getWidth() * 0.5 + random.nextInt(100) - 0.5,
                                        space.getHeight() * 0.5 + random.nextInt(100) - 0.5);
        jammerLocations.add(randomLoc);
    }
  }
  
  private void placeOptimalJammers(String csvFile) {
    BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
    
    try {
		br = new BufferedReader(new FileReader(csvFile));
        int numJammers = 0;
		while ((line = br.readLine()) != null && numJammers < NUMBER_OF_STATIC_JAMMERS) {
            double lat,lon;
            // use comma as separator
			String[] coords = line.split(cvsSplitBy);
            lat = Double.parseDouble(coords[0]);
            lon = Double.parseDouble(coords[1]);
            System.err.println("Coordinates are "+lat+" , "+lon);
            jammerLocations.add(new Double2D(lat, lon));
            numJammers += 1;
 
		}
        if (numJammers < NUMBER_OF_STATIC_JAMMERS) {
            System.err.println("Did not have enough optimal locations for all the jammers. Could only add "+numJammers+" jammers.");
        }
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
  
  }
 
  private void placeMobileJammers() {
    int numJammers = 0;
    Bag people = socialNetwork.getAllNodes();
    for (Object person : people) {
        if (((Person) person).trustPolicy == Person.TRUST_POLICY_ADVERSARY) {
            ((Person) person).trustPolicy = Person.TRUST_POLICY_ADVERSARY_JAMMER;
            System.err.println("Assigned node "+((Person) person).name + " to be a mobile jammer.");
            numJammers += 1;
        }
        if (numJammers >= NUMBER_OF_MOBILE_JAMMERS) {
            break;
        }
    }
    if (numJammers < NUMBER_OF_MOBILE_JAMMERS) {
        System.err.println("You don't have enough adversaries to establish this many mobile jammers. Please set the -na flag.");
    }
  }
 
  private void addGowallaPeopleAndSocialNetwork() {
    // Parse the social network file.
    try {
      StAndrewsSocialNetworkParser parser = 
              new StAndrewsSocialNetworkParser(GOWALLA_SOCIAL_NETWORK_FILE,
                                               '\t',
                                               0,
                                               this);
      socialNetwork = parser.getNetwork();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Parse the trace and add the traces to the people.
    boolean outOfRange; // checks if a node is in our geographic range of interest
    try {
      System.err.println("Parsing trace file " + GOWALLA_MOBILITY_TRACE_FILE);
      CSVFieldChunkReader chunkReader = 
        new CSVFieldChunkReader(GOWALLA_MOBILITY_TRACE_FILE,
                                GOWALLA_DELIMITER,
                                QUOTE_CHAR,
                                GOWALLA_LINES_TO_SKIP,
                                GOWALLA_INDEX_PERSON_ID);
      List<String[]> chunk;
      int i = 0;
      while ((chunk = chunkReader.nextChunk()) != null) {
        i++;
        outOfRange = false;
        if (i % 10000 == 0) {
          System.err.print(i + ", ");
        }
        // System.out.println("Reading chunk for person " + 
        //                    Integer.parseInt(chunk.get(0)[GOWALLA_INDEX_PERSON_ID]));
        List<Location> locations = new ArrayList<Location>();
        for (String[] line : chunk) {
          double lat = Double.parseDouble(line[GOWALLA_INDEX_LATITUDE]);
          if (lat < GOWALLA_MIN_LATITUDE || lat > GOWALLA_MAX_LATITUDE){
            outOfRange = true;
            break;
          }
          double lon = Double.parseDouble(line[GOWALLA_INDEX_LONGITUDE]);
          if (lon < GOWALLA_MIN_LONGITUDE || lon > GOWALLA_MAX_LONGITUDE){
            outOfRange = true;
            break;
          }
          String dateString = line[GOWALLA_INDEX_DATE];
          Date date = dateStringToDate(dateString);
          Location location = new Location(lat, lon, date);
          locations.add(location);
        }
        
        int id;
        if (chunk.size() > 0) {
          id = Integer.parseInt(chunk.get(0)[GOWALLA_INDEX_PERSON_ID]);
          Person person = getPersonWithID(id);
          if (person == null) {
            System.err.println("Person with id " + id + " exists in mobility but not in social network");
            System.exit(1);
          }
          
          // If the node is not inside the greater London area, so we don't add it
          // and remove it from the social network
          if (outOfRange) {
            socialNetwork.removeNode(person);
            continue;
          }
          
          MobilityTrace trace = new MobilityTrace(locations);
          person.addMobilityTrace(trace);
          System.err.println("trace has this many entries "+trace.locations.size());
          person.schedule();
          // System.err.println(chunk.size() + " check-ins for person with ID " + id);
        } else {
          System.err.println("This shouldn't happen! ASDF");
        }
          
      }
      System.err.println("The number of nodes in the network are "+ socialNetwork.getAllNodes().numObjs);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private Person getPersonWithID(int id) {
    Bag people = socialNetwork.getAllNodes();
    for (int i=0; i<people.numObjs; i++) {
      Person p = (Person) people.objs[i];
      if (p.name == id) {
        return p;
      }
    }
    return null;
  }
  
  private void addCabspottingPeopleAndRandomSocialNetwork() {
    List<String> locationTraceFilenames;
    try {
      locationTraceFilenames = getLocationTraceFilenames(traceIndexFilename); 
      // System.out.println(locationTraceFilenames);
    } catch (FileNotFoundException e) {
      System.err.println(e);
      locationTraceFilenames = new ArrayList<String>();
    }
    Iterator<String> traceIterator = locationTraceFilenames.iterator();
    for (int i=0; i<NUMBER_OF_PEOPLE; i++) {
      Person p = new Person(i, Person.TRUST_POLICY_SIGMOID_FRACTION_OF_FRIENDS, this);

      try {
        p.addMobilityTrace(traceIterator.next());
        setObjectLatLonLocation(p, p.mobilityTrace.locations.get(0));
      } catch (FileNotFoundException e) {
        System.err.println(e);
        // Well.
      }

      // See call to add social edges below. Here people are simply
      // added as entities in the network.
      socialNetwork.addNode(p);

      // Schedule the person to move, author messages, etc.
      // schedule.scheduleRepeating(p);
      p.schedule();

    }
    
    //Adds a Barabasi-Albert social graph
    addScaleFreeRandomSocialGraph();
  }


  private void addRandomSocialEdges() {
  /** Adds a uniformly random social graph-- just picks 5 nodes for each node to be connected to */
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
        // make sure that the two people are not the same and not already friends
        if (person == otherPerson || areFriends(person,otherPerson)) {
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
    
    System.err.println("The adversaries have this many friends: "+allAdversaryFriends.numObjs);
    
    
    // Make sure each adversary has the ENTIRE adversarial Bag-o-friends
    double buddiness = 1.0;
    for (Object adv : allAdversaries) {
        for (Object friend : allAdversaryFriends ) {
            if (! areFriends(adv,friend)) {
                socialNetwork.addEdge(adv, friend, new Double(buddiness));
            }
        }
    }
    
  }
  
  public boolean areFriends(Object node1, Object node2) {
    // checks if node1 and node2 are friends
    Bag myFriends = new Bag();  
    socialNetwork.getEdges(node1,myFriends);
    return bagContains(myFriends,node2);
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

    return new Double2D(simX, simY);
  }
  private Date dateStringToDate(String dateString) {
    try {
      // Dates of the form:
      //
      // 2010-05-27T22:39:52Z
      //
      SimpleDateFormat gowallaFormat =
              new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
      return gowallaFormat.parse(dateString);
    } catch (ParseException e) {
      System.err.format("Can't parse date string: '%s'\n", dateString);
      return null;
    }
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

  public ProximitySimulation(long seed) {
    super(seed);
  }

  public static Options createCommandLineOptions() {
    
    // Number of nodes
    Option numNodes   = OptionBuilder.withArgName( "int" )
                                .hasArg()
                                .withType(Number.class)
                                .withDescription(  "use this number of nodes in the simulation" )
                                .create( "nn" );
    // Number of adversaries
    Option numAdversaries   = OptionBuilder.withArgName( "int" )
                                .hasArg()
                                .withType(Number.class)
                                .withDescription(  "use this number of adversaries in the simulation" )
                                .create( "na" );
    
    // Message authorship
    Option authorship       = OptionBuilder.withArgName( "author" )
                                .hasArg()
                                .withDescription(  "use author with the given popularity in the simulation (popular,unpopular,adversarial,random)" )
                                .create( "author" );
    
    // Stationary jamming option
    Option stationaryJammers = OptionBuilder.withArgName( "int" )
                                .hasArg()
                                .withDescription( "How many stationary jammers to use" )
                                .create( "jamStationary" );
                                
    // Stationary jamming optimality
    Option optimalJammers = new Option( "jamOpt", "Should stationary jammers use optimal placement?" );
    
    // jamming radius
    Option jamRadius =  OptionBuilder.withArgName( "double" )
                                .hasArg()
                                .withDescription( "Radius of jammer(s)" )
                                .create( "radius" );
    
    // Mobile jamming option
    Option mobileJammers = OptionBuilder.withArgName( "int" )
                                .hasArg()
                                .withDescription( "How many mobile jammers to use" )
                                .create( "jamMobile" );
                                            
    Options options = new Options();
    options.addOption( numNodes );
    options.addOption( numAdversaries );
    options.addOption( authorship );
    options.addOption( stationaryJammers );
    options.addOption( optimalJammers );
    options.addOption( mobileJammers );
    options.addOption( jamRadius );
    
    return options;
  }
  
  public static void parseOptions(String[] args, Options options) {
    // create the parser
    GnuParser parser = new GnuParser();
    try {
        // parse the command line arguments
        CommandLine line = parser.parse( options, args );
        
        // has the number of nodes argument been passed?
        NUMBER_OF_PEOPLE = parseIntegerArg( line, "nn" , NUMBER_OF_PEOPLE );
        if (NUMBER_OF_PEOPLE < 1) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ProximitySimulation", options, true );
            System.err.println( "\n\nYou must enter a valid number of people." );
            return;
        }
        
        // Number of adversaries (default=0)
        NUMBER_OF_ADVERSARIES = parseIntegerArg( line, "na" , NUMBER_OF_ADVERSARIES );
        NUMBER_OF_ADVERSARIES = Math.min( NUMBER_OF_PEOPLE, NUMBER_OF_ADVERSARIES);
        
        // author popularity (default = random)
        if( line.hasOption( "author" ) ) {
            // initialise the member variable
            String popularity = line.getOptionValue( "author" );
            if (popularity.equals("random")) {
                messageAuthor = RANDOM_AUTHOR;
            } else if (popularity.equals("popular")) {
                messageAuthor = POPULAR_AUTHOR;
                popularAuthor = true;
            } else if (popularity.equals("unpopular")) {
                messageAuthor = POPULAR_AUTHOR;
                popularAuthor = false;
            } else if (popularity.equals("adversarial")) {
                messageAuthor = ADVERSARIAL_AUTHOR;
            } else {
                System.err.println("Not a valid popularity flag.");
            }
        }
        
        // number of stationary jammers
        NUMBER_OF_STATIC_JAMMERS = parseIntegerArg( line, "jamStationary" , NUMBER_OF_STATIC_JAMMERS );
        if (NUMBER_OF_STATIC_JAMMERS > 0) {
            staticJamming = true;
        }
        
        // stationary jammer optimality
        if( line.hasOption( "jamOpt" ) ) {
            staticJammingOptimal = true;
        }
        
        //Mobile jammers
        NUMBER_OF_MOBILE_JAMMERS = parseIntegerArg( line, "jamMobile" , NUMBER_OF_MOBILE_JAMMERS );
        if (NUMBER_OF_MOBILE_JAMMERS > 0) {
            mobileJamming = true;
        }
        
        //Jammers' radius
        JAMMING_RADIUS = parseDoubleArg( line, "radius" , JAMMING_RADIUS );
        
    } 
    catch( Exception exp ) {
        // oops, something went wrong
        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
    }
  }
  
  public static int parseIntegerArg(CommandLine line, String argName, int variable) {
    // parse an input with an integer value
    if( line.hasOption( argName ) ) {
        int num = -1;
        try {
            num = Integer.parseInt( line.getOptionValue( argName ));
        }
        catch (NumberFormatException e) {
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }
        
        if (num > 0) {
            variable = num;
        }
        System.err.println("set " + argName + " to "+variable);
        
    }
    return variable;
  
  }
  
  public static double parseDoubleArg(CommandLine line, String argName, double variable) {
    // parse an input with a double value
    if( line.hasOption( argName ) ) {
        double num = 0.0;
        try {
            num = Double.parseDouble( line.getOptionValue( argName ));
        }
        catch (NumberFormatException e) {
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }
        
        if (num > 0.0) {
            variable = num;
        }
        System.err.println("set " + argName + " to "+variable);
        
    }
    return variable;
  
  }
  
  public static void main(String[] args) {
  
    
    // create Options object
    Options options = createCommandLineOptions();
    
    // Parse the inputs
    parseOptions(args,options);
    
    
    if (NUMBER_OF_PEOPLE > 0) {
        //Run the simulation
        doLoop(ProximitySimulation.class, args);
        System.exit(0);
    }
  }
}
