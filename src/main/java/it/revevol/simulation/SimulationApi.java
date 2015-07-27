package it.revevol.simulation;

import static it.revevol.simulation.service.OfyService.ofy;
import it.revevol.simulation.domain.SimulationResult;
import it.revevol.simulation.domain.User;
import it.revevol.simulation.exception.LoginInvalidException;
import it.revevol.simulation.exception.RegisterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.repackaged.com.google.api.client.util.Base64;

import javax.inject.Named;

import com.googlecode.objectify.Key;

import org.apache.log4j.Logger;

/**
 * Defines simulation APIs.
 */
@Api(name = "simulation", 
	version = "v1", 
	scopes = { Constants.EMAIL_SCOPE }, 
	clientIds = {
        Constants.WEB_CLIENT_ID, 
        Constants.API_EXPLORER_CLIENT_ID },
        description = "API for the Revevol Simulation Backend application.")

public class SimulationApi {
	
	private static final Logger log = Logger.getLogger(SimulationApi.class.getName());
	private static final int DIVIDER = 1000; // occurrence_value : occurrence_percentage = 10000 : 100
	
	/**
	 * Executes the simulation of generating 100000 nums between 0 and 50.
	 * 
	 * @return An object with the results of the simulation
	 */
    @ApiMethod(name = "executeSimulation", path = "executeSimulation", httpMethod = HttpMethod.GET)
    public SimulationResult executeSimulation() {
    	SimulationResult simResult = new SimulationResult();

    	ArrayList<Integer> numResult = new ArrayList<Integer>();
    	ArrayList<Integer> numbers = new ArrayList<Integer>();
    	
    	long startTime = System.currentTimeMillis();
    	log.info("Simulation launched at : " + startTime);
    	
    	//simulate the 100 tasks with 100 calls of the function which generate 1000nums each
    	for(int i=0;i<100;i++){
    		
    		//generates the 1000 nums
    		numbers = generateRandomNumbers();
    		numResult.addAll(numbers);
    	}
    	long endTime = System.currentTimeMillis();
    	
    	log.info("Simulation finished at : "+ endTime);
    	
    	long executionTime = endTime-startTime;
    	log.info("Simulation execution time is : " + startTime);
    	
    	//how many different numbers have been generated
    	simResult.setDifferentNums(countDifferentNums(numResult));
    	
    	HashMap<Integer,Integer> occurrence = calculateOccurrence(numResult);
    	HashMap<Integer, Double> occurrencePercentage = obtainPercentageMap(occurrence); 
    	
    	//how many times a number has been generated
    	simResult.setOccurence(occurrence);
    	
    	//how many times a number has been generated (in percentage)
    	simResult.setOccurencePercent(occurrencePercentage);
    	
    	//the most frequent number generated
    	simResult.setMostFrequent(mostFrequentNumber(occurrence));
    	
    	//the execution time
    	simResult.setExecutionTime(executionTime);
    	
		return simResult;
    }

    /**
     * Calculate how many times a number has been generated
     * @param numsArray The array of generated nums
     * @return The hashmap for number/occurrence
     */
    private static HashMap<Integer, Integer> calculateOccurrence(ArrayList<Integer> numsArray){
    	
    	//The map number/occurrence for every generated number
    	HashMap<Integer, Integer> occurrence = new HashMap<Integer, Integer>();

    	for(Integer i : numsArray){
    		if(occurrence.containsKey(i)){
    			Integer value = occurrence.get(i);
    			value ++;
    			occurrence.put(i, value);
    		}
    		else{
    			occurrence.put(i, new Integer(1));
    		}
    	}
    	
    	return occurrence;
    }
    
    /**
     * Generates a HashMap of number and its occurrence expressed in percentage (considering the total
     * number of generated numbers is 100000)
     * @param occurrence The Hashmap number / occurrence
     * @return The HashMap number/occurrence in %
     */
    private static HashMap<Integer, Double> obtainPercentageMap(HashMap<Integer, Integer> occurrence){
    	HashMap<Integer, Double> occurrencePercentage = new HashMap<Integer,Double>();
    	
    	//for every entry of the hashmap takes its value and calculate its value in percentage
    	for( Map.Entry<Integer, Integer> entry : occurrence.entrySet()){
    		Integer key = entry.getKey();
    		Integer value = entry.getValue();
    		
    		double valuePercentage = ((double) value) / DIVIDER; // value : x = 10000 : 100
    		
    		occurrencePercentage.put(key, valuePercentage);
    	}
    	
    	return occurrencePercentage;
    }
    
    /**
     * Returns the most frequent number in an hashmap (the number with the highest value)
     * @param hashNums A hashmap of number/occurrence
     * @return The key with highest value
     */
    private static int mostFrequentNumber(HashMap<Integer,Integer> hashNums){
    	
    	Map.Entry<Integer, Integer> maxEntry = null;

    	//For every entry in the hashmap, confront the values to find the Entry with the highest value
    	for (Map.Entry<Integer, Integer> entry : hashNums.entrySet()){
    	    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
    	        maxEntry = entry;
    	    }
    	}
    	
    	return maxEntry.getKey();
    	
    }
    
    /**
     * Counts how many different numbers have been generated.
     * @param numsArray An array of integers
     * @return The number of different random numbers generated
     */
    private static int countDifferentNums(ArrayList<Integer> numsArray){
    	//An HashSet cannot contain duplicates!
    	HashSet<Integer> noDupSet = new HashSet<Integer>();
    	
    	for(Integer i : numsArray){
    		//Adds the specified element to the set if it is not already present
    		noDupSet.add(i);
    	}
    	
    	return noDupSet.size();
    }
    
    /**
     * Generates an array of 100 random integer numbers
     * @return The array of random numbers
     */
    private static ArrayList<Integer> generateRandomNumbers(){
    	ArrayList<Integer> numbers = new ArrayList<Integer>();
    	Random rand = new Random();
    	
    	//generate 1000 random integers between 0 and 50
    	for(int i=0;i<1000;i++){
    		numbers.add(rand.nextInt(51));
    	}
    	
    	return numbers;
    }
    
    
    /**
     * An API method for register operation.
     * Takes the user data and save the User object into the datastore
     * 
     * @param username
     * @param password
     * @param firstname
     * @param lastname
     * @return The User object registered
     * @throws RegisterException
     */
    @ApiMethod(name = "register", path = "register", httpMethod = HttpMethod.POST)
    public User register(@Named("username") String username, @Named("password") String password, 
    		@Named("firstname") String firstname, @Named("lastname") String lastname) throws RegisterException{
    	
    	String encoded = encode(username, password);
    	
    	//get the user from the encoded string
    	User user = getUser(encoded);
    	
    	if(user == null){
    		// Create a new User entity
    		user = new User(username, password,encoded,firstname,lastname);
    		
    		// Save the User entity in the datastore
    		ofy().save().entity(user).now();
    	}
    	//if the user already exists
    	else{
    		throw new RegisterException("User is already present");
    	}   
    	
    	log.info("User "+user.getUsername() + " registered !");
    	// Return the user
    	return user;
    }

    /**
     * An API method for login operation.
     * Gets the user from the username and password (encoded together) and returns the User object
     * 
     * @param username
     * @param password
     * @return The user object
     * @throws LoginInvalidException
     */
    @ApiMethod(name = "login", path = "login", httpMethod = HttpMethod.GET)
    public User login(@Named("username") String username, @Named("password") String password) 
    		throws LoginInvalidException{
    	
    	User user = getUser(encode(username, password));
    	
    	if(user == null){
    		throw new LoginInvalidException("User is not valid");
    	}
    	
    	log.info("User " + user.getUsername() + " logged in !");
    	
    	return user;
    }

    /**
     * Takes an encoded "username:password" string and get the user from the datastore using the
     * encoded string as the key
     * 
     * @param encoded
     * @return
     */
    public static User getUser(String encoded) {
    	
    	//Loads the User entity from the datastore 
        Key key = Key.create(User.class,encoded);
        User user = (User) ofy().load().key(key).now();

        return user;
    }
    
    
    /**
     * Encoding the username and password in a *not* robust way for a really basic authentication
     * @param username
     * @param pwd
     * @return
     */
    public static String encode(String username, String pwd){
	  final String pair = username + ":" + pwd;
	  final byte[] encodedBytes = Base64.encodeBase64(pair.getBytes());
	  
	  return new String(encodedBytes);
    }
    
}
