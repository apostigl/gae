'use strict';

/**
 * The root simulationApp module.
 *
 * @type {simulationApp|*|{}}
 */
var simulationApp = simulationApp || {};

/**
 * @ngdoc module
 * @name conferenceControllers
 *
 * @description
 * Angular module for controllers.
 *
 */
simulationApp.controllers = angular.module('simulationControllers', []);


/**
 * @ngdoc controller
 * @name RootCtrl
 *
 * @description
 * The root controller having a scope of the body element and methods used in the application wide
 *
 */
simulationApp.controllers.controller('RootCtrl', function ($scope, $location, oauth2Provider, sharedProperties) {

    /**
     * Returns if the viewLocation is the currently viewed page.
     *
     * @param viewLocation
     * @returns {boolean} true if viewLocation is the currently viewed page. Returns false otherwise.
     */
    $scope.isActive = function (viewLocation) {
        return viewLocation === $location.path();
    };

    /**
     * Returns the signedIn/loggedIn state.
     * @returns true if signedIn or loggedIn, false otherwise.
     */
    $scope.getSignedInState = function () {
        return oauth2Provider.signedIn || sharedProperties.getLoggedIn();
    };

    /**
     * Calls the OAuth2 authentication method.
     */
    $scope.signIn = function () {
        oauth2Provider.signIn(function () {
            gapi.client.oauth2.userinfo.get().execute(function (resp) {
                $scope.$apply(function () {
                    if (resp.email) {
                        oauth2Provider.signedIn = true;
                        $scope.alertStatus = 'success';
                        $scope.rootMessages = 'Logged in with ' + resp.email;
                        
                        $location.path('/index');
                    }
                });
            });
        });
    };

    /**
     * Render the signInButton and restore the credential if it's stored in the cookie.
     * (Just calling this to restore the credential from the stored cookie. So hiding the signInButton immediately
     *  after the rendering)
     */
    $scope.initSignInButton = function () {
        gapi.signin.render('signInButton', {
            'callback': function () {
                jQuery('#signInButton button').attr('disabled', 'true').css('cursor', 'default');
                if (gapi.auth.getToken() && gapi.auth.getToken().access_token) {
                    $scope.$apply(function () {
                        oauth2Provider.signedIn = true;
                    });
                }
            },
            'clientid': oauth2Provider.CLIENT_ID,
            'cookiepolicy': 'single_host_origin',
            'scope': oauth2Provider.SCOPES
        });
    };

    /**
     * Signs out the user from both authentication ways.
     */
    $scope.signOut = function () {
        if(oauth2Provider.signedIn){
        	oauth2Provider.signOut();
        }
        else{
        	$scope.logOut();
        }
        $scope.alertStatus = 'success';
        $scope.rootMessages = 'Logged out';
        
        $location.path('/index');
    };
    
    /**
     * Logs out the user for standard authentication (username / pwd)
     */
    $scope.logOut = function () {
    	sharedProperties.setLoggedIn(false);
    };
    

    /**
     * Collapses the navbar on mobile devices.
     */
    $scope.collapseNavbar = function () {
        angular.element(document.querySelector('.navbar-collapse')).removeClass('in');
        
        //$scope.rootMessages = '';
    };
    
});







/**
 * @ngdoc controller
 * @name LoginCtrl
 *
 * @description
 * The Login controller for standard authentication (username + password)
 * 
 * TODO Should implement a stronger authentication method with control over session!
 */

simulationApp.controllers.controller('LoginCtrl', function ($scope, $location, sharedProperties) {
	/**
     * Calls the register API to register an user into the webapp (using datastore).
     */
    $scope.register = function (user) {
        gapi.client.simulation.register(user).
            execute(function (resp) {
                $scope.$apply(function () {
                    if (resp.error) {
                        // The request has failed.
                        var errorMessage = resp.error.message || '';
                        sharedProperties.setLoggedIn(false);
                        $scope.$root.rootMessages = 'Failed to signin : ' + errorMessage;
                        $scope.$root.alertStatus = 'warning';
                    } else {
                        // The request has succeeded.
                    	sharedProperties.setLoggedIn(true);
                        $scope.$root.rootMessages = 'Signed in with ' + resp.username;
                        $scope.$root.alertStatus = 'success';
                        
                        //redirect to index
                        $location.path('/index');
                    }
                });
            });
    };
    
    
    /**
     * Calls the login API to login an user to the webapp.
     */
    $scope.login = function (user) {
        gapi.client.simulation.login(user).
            execute(function (resp) {
                $scope.$apply(function () {
                    if (resp.error) {
                    		//not valid user
                    		sharedProperties.setLoggedIn(false);
                    		$scope.$root.rootMessages = 'User is not valid !';
                    		$scope.$root.alertStatus = 'warning';
                    		//return;
                    	}
                    	else{
                    		//valid user
                    		sharedProperties.setLoggedIn(true);
                    		$scope.$root.rootMessages = 'Logged in with ' + resp.username;
                            $scope.$root.alertStatus = 'success';
                            
                            $location.path('/index');
                    	}
                });
            });
    };
    
});



/**
 * @ngdoc controller
 * @name SimulationCtrl
 *
 * @description
 * The Simulation controller for launch the simulation and show the results
 */

simulationApp.controllers.controller('SimulationCtrl', function ($scope, $location, $log) {
	/**
     * Calls the simulation API to launch the simulation
     */
    $scope.launchSimulation = function () {
        gapi.client.simulation.executeSimulation().
            execute(function (resp) {
                $scope.$apply(function () {
                    if (resp.error) {
                        // The request has failed.
                        var errorMessage = resp.error.message || '';
                    } else {
                        // The request has succeeded.
                        $scope.$root.result = resp;
                        $location.path('/result');
                    }
                });
            });
    };
});

simulationApp.controllers.controller('SimulationResultCtrl', function ($scope, $location, $log) {
	
	/**
     * This is the function called when the result page is initiated.
     */
	 $scope.simulationResult = function () {
		 
 /**
  * START OF PIE CHART - OCCURENCE
  */
		 var ctxPie = document.getElementById("pieChart").getContext("2d");
		 var occurrencePercent =  $scope.$root.result.occurencePercent;
		 
		 //calculate object size
		 var occurrence_size = 0, key;
		 for (key in occurrencePercent) {
		    if (occurrencePercent.hasOwnProperty(key))
		        	occurrence_size++;
		 }
		  
		 var dataPie = [];
		 //generate occurrence data for pie chart
		 for (var i = 0; i < occurrence_size; i++) {
		     var obj = {
		    		 label: "Number: " + i,
		    		 value: occurrencePercent[i],
		    		 color: "#" + Math.random().toString(16).slice(2, 8), //generate random hex colour
		     };
		     dataPie.push(obj);
		}
		 
		var myPieChart = new Chart(ctxPie).Pie(dataPie,{ animation: true, animationSteps: 20, animationEasing: "easeInBounce",tooltipFontSize: 15,tooltipTemplate: "<%if (label){%><%=label%> - <%}%><%= value %>%"});
		 
 /**
  * END OF PIE CHART - OCCURENCE
  */
		 
		 
		 
 /**
  * START OF BAR CHART - EXECUTION TIME
  */
		 var ctxBar = document.getElementById("barChart").getContext("2d");
		 var executionTime =  $scope.$root.result.executionTime;
		 
		 var dataBar = {
				    labels: [],
				    datasets: [
				        {
				            fillColor: "rgba(220,220,220,0.5)",
				            strokeColor: "rgba(220,220,220,0.8)",
				            highlightFill: "rgba(220,220,220,0.75)",
				            highlightStroke: "rgba(220,220,220,1)",
				            data: [executionTime]
				        },
				    ]
				};
		 
		 var myBarChart = new Chart(ctxBar).Bar(dataBar, {  tooltipTemplate : "<%= value %> ms"});
		 
 /**
  * END OF BAR CHART - EXECUTION TIME
  */
		 
 /**
  * START OF BAR CHART - MOST FREQUENT NUMBER
  */
		 var ctxMFBar = document.getElementById("mfBarChart").getContext("2d");
		 var mostFrequentNumber =  $scope.$root.result.mostFrequent;
		 var numbers = [];
		 
		 //fill array of generated numbers
		 for(var i=0; i <=50; i++){
			 numbers.push(""+i);
		 }
		 
		 var occurrence =  $scope.$root.result.occurence;
		 
		 var MFdataBar = {
				    labels: numbers,
				    datasets: [
				        {
				            fillColor: "rgba(220,220,220,0.5)",
				            strokeColor: "rgba(220,220,220,0.8)",
				            highlightFill: "rgba(220,220,220,0.75)",
				            highlightStroke: "rgba(220,220,220,1)",
				            data: occurrence
				        },
				    ]
				};
		 
		 var myBarChart = new Chart(ctxMFBar).Bar(MFdataBar, {  tooltipTemplate : "<%= value %> "});
		 
 /**
  * END OF BAR CHART - MOST FREQUENT NUMBER
  */		 
		 
	 }
	
});

/**
 * @ngdoc controller
 * @name LogCtrl
 *
 * @description
 * The Log Controller for display log information in a webpage
 */

simulationApp.controllers.controller('LogCtrl', function ($scope, $location, $http) {
	/**
     * Called when the log page is displayed. Use a dedicated servlet call to retrieve logs info.
     */
    $scope.displayLog = function (url) {
    	$http.get(url).success(function(data) {
    		 angular.element(document.querySelector('#log')).html(data);
    	});
   }
});
