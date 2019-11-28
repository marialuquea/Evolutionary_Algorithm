package tspsolver;
/*Simulated Annealing
 * This program reads in a TSP file and attempts to find the minimum tour length
 * using simulated annealing.
 * There are 2 move-operators built in: 
 *    swap - this just picks 2 cities at random and swaps 
 *           their positions in the tour
 *    twoOpt: this picks cities at random and then reverses the order of the
 *            existing tour between these two city positions.
 *
 *  To Run;
 *      java SimAnneal filename iterations startTemp finalTemp cooolingRate v
 *
 *  The first parameter is the name of a TSP file
 *  The second parameter is the number of iterations to run the algorithm for
 *  The third parameter is the start temperature (e.g 300)
 *  The 4th parameter is the final temperature (e.g 0.0)
 *  The 5th parameter is the cooling rate (normally between 0.90 and 1.0) 
 *  The last parameter is optional. If given, a verbose report is written
 *  as the algorithm runs
 */ 



import java.io.*;
import java.text.*;

public class simanneal{
    double coolingRate;
    double startTemp;
    double finalTemp;
    double currentTemp;
    int maxIterations;
    boolean isInitialised=false;
    double currentFitness;
    int numIter;
    int soln[];
    int newSoln[];
    int numCities;
    double xcoord[];
    double ycoord[];
    boolean verbose;
    int NEW=1;
    int OLD=2;
    boolean useSwap = true;


    public simanneal(String myfile, int it, double s, double f, double r, boolean v, boolean op){
	// initialise parameters

	maxIterations=it;
	startTemp = s;
	finalTemp = f;
	coolingRate = r;
	loadData(myfile);
	verbose=v;
    useSwap = op;

    
    System.out.println("Solver is SIMULATED ANNEALING");
    }

    
  public void runSA(){
     
	double fitness;
       	int iter=0;
	double randomNum;
	double diff,prob;

	DecimalFormat df = new DecimalFormat ("0.00");
	// initialise with a random solution
	initialise();
	currentTemp = startTemp;

	// run algorithm with parameters chosen 
	while (iter<maxIterations && currentTemp > finalTemp){

	    // ******************************************************	 
	    // apply neighbourhood function to get a potential new solution
	    // you can replace this method with any of the methods
	    // provided, or code your own!
		// 
		//Two alternative mutation methods are provided:
		//  swapTwoCities and twoOpt
		// 
	    // ******************************************************* 
	    
		if (useSwap)
			swapTwoCities();
		else
			twoOpt(); 

	    fitness=getTourLength(newSoln);
	    
	    // if the new one is better than the old one (shorter distance)
	    // then replace it
	    
	    if (fitness <= currentFitness){
		replaceSolution();
		currentFitness=fitness;
		if (verbose){
		    System.out.println("iteration " + iter + " temp " + df.format(currentTemp)+ " found better solution: " + df.format(currentFitness));
		}
	    } else {
		// accept worse solution with some probability
		diff = fitness-currentFitness;
		randomNum = Math.random();
		prob = Math.exp(-(double)Math.abs(diff)/currentTemp);
	

		if (prob>randomNum){
		    replaceSolution();
		    currentFitness=fitness;
		    if (verbose)
			System.out.println("iteration " + iter + " temp " + df.format(currentTemp) + " accepted worse solution " + df.format(currentFitness));
		}

		
	    }
	
	    // decrease temperature
	    currentTemp*=coolingRate;

	    // increment iteration counter
	    iter++;
	}

	
	// print  the  final solution
	if (verbose){
	    System.out.println("\n\nFinal Solution:");
	    printTour(OLD);
	}
	
	System.out.println("Best tour length " +  df.format(currentFitness));
    } 
	     

    public void printTour(int which){
	for (int i=0;i<soln.length;i++){
	    if (which==OLD)
		System.out.print(soln[i] + "-");
	    else
		System.out.print(newSoln[i] + "-");
	}
	System.out.println("\n");
    }
    
    // this method initialises a solution with
    // a random permutation
    
    public void initialise(){


	DecimalFormat df = new DecimalFormat ("0.00");

	// create an array to hold initial solution and new solutions
	soln= new int[numCities];
	newSoln= new int[numCities];

	// and set it to a random permutation
	permutation();
	
       	currentFitness=getTourLength(soln);	
	System.out.println("initial fitness = "+df.format(currentFitness));
	if (verbose) printTour(OLD);
    }

    // this method creates a random permutation  1..N
    public void permutation(){

   
      // insert integers 1..N
      for (int i = 0; i < numCities; i++)
         soln[i] = i+1;

      // shuffle
      for (int i = 0; i < numCities; i++) {
         int r = (int) (Math.random() * (i+1));     // int between 1 and N
         int swap = soln[r];
         soln[r] = soln[i];
         soln[i] = swap;
      }

      
    }

  
    /*
     * This is a simple neighbourhood function that picks two random points
     * and then swaps the cities
     */
    
    
    public void swapTwoCities(){
	// copy the current solution
	
	for (int i=0;i<numCities;i++)
	    newSoln[i]=soln[i];


	// mutate it
	
	int place1, place2;
	
	place1 = (int)(Math.random()*numCities);
	place2 = (int)(Math.random()*numCities);
	    
	
	int swap = soln[place1];
	newSoln[place1] = soln[place2];
	newSoln[place2] = swap;
   
    }

    // twoOpt
    // this is a well known heuristic for solving TSPS.
    // it randomly selects two edges in the tour, e.g. (a-b) and (c-d)
    // breaks them, and then reconnects them in a different way, e.g 
    // (a-c),(b-d) or (a-d)(b-c)
    // Essentially, this is performed by randomly picking two points
    // in the solution and the reversing the tour between them
    

    public void twoOpt(){
       

	// copy the current solution
	
	for (int i=0;i<numCities;i++)
	    newSoln[i]=soln[i];


	// pick two places
	int place1, place2;
	
	place1 = (int)(Math.random()*numCities);
	place2 = (int)(Math.random()*numCities);
	    
	// if place2<place1, swap them
	if (place2<place1){
	    int swap = place1;
	    place1=place2;
	    place2=swap;
	}
	
	// create a temporary array that holds all the values between place1 and place2
	int size = place2-place1+1;
	int[] temp = new int[size];
	for (int i=0;i<size;i++)
	    temp[i] = soln[place1+i];

	// now replace the cities in newSoln with those in temp but in reverse order
	int start=0;
	for (int i=place2;i>=place1;i--){
	    newSoln[i] = temp[start];
	    start++;
	}      
        
    }

    // replace the old soution with the new solution
    public void replaceSolution(){
	for (int j=0;j<numCities;j++)
	    soln[j] = newSoln[j];
    }


    public double getTourLength(int []someSolution){
	double fitness=0;
	
	double x1,x2,y1,y2;
	int k;
	// calculate the total length of the tour

	for (int j=0;j<someSolution.length;j++){
	    int id1=someSolution[j];
	    if (j==someSolution.length-1) 
		k=0;
	    else
		k=j+1;
	    int id2 = someSolution[k];
	    
	    x1=xcoord[id1];
	    y1=ycoord[id1];
	    x2=xcoord[id2];
	    y2=ycoord[id2];
	    

	    double dist = Math.sqrt( (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	    fitness+=dist;
	}
	   
	return(fitness);
    }	

    // read  in the TSP data

  public void loadData(String myFile){
      System.out.println("file is "+myFile);
      try{
	    
	  StreamTokenizer st = new StreamTokenizer(new FileReader(myFile));
	  boolean foundDim=false;
	  st.wordChars(33,126);
	  while (!foundDim){
	    // read until we find the DIMENSION: 
	    int token = st.nextToken();
	   
	    if (token == StreamTokenizer.TT_WORD){
		String theword  = (String)st.sval;
		if (theword.equals("DIMENSION:"))
		    foundDim=true;
		else 
		    st.nextToken();
	    } else 
		st.nextToken();
	    if (token == StreamTokenizer.TT_EOF)
		break;
	
	  }	

	  // next thing should be a number then (numCities)
	  int token = st.nextToken();
	  if (st.ttype != StreamTokenizer.TT_NUMBER){
		// error!
	      System.out.println ("error ? token is not a number");
	      System.exit(0);
	    } else {
		numCities = (int)st.nval;
		System.out.println("Num cities is "+numCities);
		xcoord = new double[numCities+1];
	        ycoord = new double[numCities+1];
	    }
	  
	    // now skip until we find the NODE_COORD
	  boolean foundCoords=false;
	  
	  while (!foundCoords){
	      token = st.nextToken();
	      if (token == StreamTokenizer.TT_NUMBER){
		  System.out.println ("Error reading file " + st.nval);
		  System.exit(0);
	      }
      
	      if (st.ttype== StreamTokenizer.TT_WORD){
		  String theword = (String)st.sval;
		  if (theword.equals("NODE_COORD_SECTION"))
		      foundCoords=true;
		  else
		      st.nextToken();
	      } else
		st.nextToken();
	      
	  }
	
	    int i;
	    // loop over data then and read coords
	    for (i=0;i<numCities;i++){
		st.nextToken();
		int id = (int)st.nval;
		st.nextToken();
		double x = (double)st.nval;	
		st.nextToken();
		double y = (double)st.nval;
		xcoord[id] =x;
		ycoord[id]=y;
		
	    }

	    	    
	}
	catch (Exception e){
	    System.out.println("Error reading problem file " + myFile);
	}
    }

}



