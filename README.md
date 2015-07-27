GoogleAppEngine Simulation Test
===============================

A simple web application developed to understand how GoogleAppEngine and AngularJS work together. 
-------------------------------------------------------------------------------------------------

## Features:

* Login/logout interface with Google credentials and standard username/password form.
* Detailed log accessible via web interface.
* Interface for a simulation launch. The simulation is composed of a series of 100 batch in which every task shall generate 1000 random numbers between 0 and 50.
* Interface for visualize simulation results:

	1. How many different numbers have been generated.
	2. Percentage of occurrence of numbers between 0 and 50 (eg: the number 2 appeared 5% of the time).
	3. What is the most frequent number generated.
	4. What is the total simulation time.

* Results shall be displayed using charts loaded in a second page relative of the simulation launch page.
* Implement the GUI using a responsive design framework like Bootstrap/ AngularJS.