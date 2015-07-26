package it.revevol.simulation.spi;

import static it.revevol.simulation.service.OfyService.ofy;
import static org.junit.Assert.*;

import it.revevol.simulation.domain.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.revevol.simulation.SimulationApi;
import it.revevol.simulation.exception.LoginInvalidException;
import it.revevol.simulation.exception.RegisterException;

/**
 * TODO
 * Tests for SimulationeApi API methods.
 * 
 */
public class SimulationApiTest {

    private static final String USER_ID = "angelo";
    private static final String USER_PASSWORD = "password";
    private static final String USER_FIRSTNAME = "angelo";
    private static final String USER_LASTNAME = "postiglione";
    private User user;
    private SimulationApi simulationApi;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        simulationApi = new SimulationApi();
    	String encoded = SimulationApi.encode("angelo","password");
        user = new User("angelo","password",encoded,"angelo","postiglione");
    }

    @After
    public void tearDown() throws Exception {
        ofy().clear();
        helper.tearDown();
    }
    
    @Test(expected = LoginInvalidException.class)
    public void testLoginWithoutUser() throws Exception {
    	simulationApi.login(null,null);
    }

    @Test(expected = RegisterException.class)
    public void testRegistrationUserAlreadyPresent() throws Exception {
    	simulationApi.register("angelo","password","angelo","postiglione");
    	simulationApi.register("angelo","password","angelo","postiglione");
    }
    
    @Test
    public void testLoginSuccess() throws Exception {
    	simulationApi.register("angelo","password","angelo","postiglione");
    	User user = simulationApi.login("angelo","password");
    	
    	assertNotNull(user);
    }
    
    @Test
    public void testGetUserFirstTime() throws Exception {
    	String encoded = SimulationApi.encode("angelo","password");
        User user = ofy().load().key(Key.create(User.class, encoded)).now();
        
        assertNull(user);
        
        user = SimulationApi.getUser(encoded);
        assertNull(user);
    }
    
    
    @Test
    public void testSaveUser() throws Exception {
        // Save the user for the first time.
    	String encoded = SimulationApi.encode("angelo","password");
        User user = simulationApi.register("angelo","password","angelo","postiglione");
        
        // Check the return value first.
        assertEquals(USER_ID, user.getUsername());
        assertEquals(USER_PASSWORD, user.getPassword());
        assertEquals(USER_FIRSTNAME, user.getFirstname());
        assertEquals(USER_LASTNAME, user.getLastname());
        
        // Fetch the Profile via Objectify.
        user = ofy().load().key(Key.create(User.class, encoded)).now();
        
        assertEquals(USER_ID, user.getUsername());
        assertEquals(USER_PASSWORD, user.getPassword());
        assertEquals(USER_FIRSTNAME, user.getFirstname());
        assertEquals(USER_LASTNAME, user.getLastname());
    }
}
