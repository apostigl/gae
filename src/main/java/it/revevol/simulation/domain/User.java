package it.revevol.simulation.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The User object for the Simulation web application.
 * An user can register and login into the application. He can also launch the simulation and inspect the results.
 * 
 * @author Angelo
 *
 */

@Entity
public class User {
	
	//The encoded string user:pwd is the key of the object in the datastore
	@Id String encoded;

	//Biodata
	String username;
	String password;
	String firstname;
	String lastname;
	
	/**
	 * Public constructor for User.
	 * @param username, password, encoded, firstname, lastname
	 */
	public User (String username, String password, String encoded, String firstname, String lastname) {
		this.username = username;
		this.password = password;
		this.encoded = encoded;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getEncoded() {
		return encoded;
	}
	
	public void setEncoded(String encoded) {
		this.encoded = encoded;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
    

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
     * Just making the default constructor private.
     */
    private User() {}

}
