'use strict';

/**
 * @ngdoc object
 * @name simulationApp
 * @requires $routeProvider
 * @requires simulationControllers
 *
 * @description
 * Root app, which routes and specifies the partial html and controller depending on the url requested.
 *
 */
var app = angular.module('simulationApp', ['simulationControllers', 'ngRoute'])
		.config(['$routeProvider', function ($routeProvider) {
            $routeProvider.
                when('/login', {
                    templateUrl: '/partials/login.html',
                    controller: 'LoginCtrl'
                }).
                when('/register', {
                    templateUrl: '/partials/register.html',
                    controller: 'LoginCtrl'
                }).
                when('/logs', {
                    templateUrl: '/partials/logs.html',
                    controller: 'LogCtrl'
                }).
                when('/simulation', {
                    templateUrl: '/partials/simulation.html',
                    controller: 'SimulationCtrl'
                }).
                when('/result', {
                    templateUrl: '/partials/result.html',
                    controller: 'SimulationResultCtrl'
                }).
                when('/', {
                    templateUrl: '/partials/home.html'
                }).
                otherwise({
                    redirectTo: '/'
                });
        }])
        .service('sharedProperties', function () { //use a service to share the logged in/out status among controllers
	        var loggedIn = false;
	
	        return {
	            getLoggedIn: function () {
	                return loggedIn;
	            },
	            setLoggedIn: function(value) {
	            	loggedIn = value;
	            }
	        };
        });



/**
 * @ngdoc service
 * @name oauth2Provider
 *
 * @description
 * Service that holds the OAuth2 information shared across all the pages.
 *
 */
app.factory('oauth2Provider', function () {
    var oauth2Provider = {
        CLIENT_ID: '923418208109-fip9ob5juikktpbo2t6au1vb3n9ruh57.apps.googleusercontent.com',
        SCOPES: 'https://www.googleapis.com/auth/userinfo.email profile',
        signedIn: false
    };

    /**
     * Calls the OAuth2 authentication method.
     */
    oauth2Provider.signIn = function (callback) {
        gapi.auth.signIn({
            'clientid': oauth2Provider.CLIENT_ID,
            'cookiepolicy': 'single_host_origin',
            'accesstype': 'online',
            'approveprompt': 'auto',
            'scope': oauth2Provider.SCOPES,
            'callback': callback
        });
    };

    /**
     * Logs out the user.
     */
    oauth2Provider.signOut = function () {
        gapi.auth.signOut();
        // Explicitly set the invalid access token in order to make the API calls fail.
        gapi.auth.setToken({access_token: ''})
        oauth2Provider.signedIn = false;
    };

    return oauth2Provider;
});
