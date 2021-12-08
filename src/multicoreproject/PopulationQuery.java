package multicoreproject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner; 
import java.util.Random;
import java.time.Instant;
import java.io.File;  // Import the File class
import java.util.Scanner; // Import the Scanner class to read text files

import java.util.logging.Level;
import java.util.logging.Logger;


public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;
	
	// parse the input file into a large array held in a CensusData object
	public static CensusData parse(String filename) {
		CensusData result = new CensusData();                
		
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(filename));
            
            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)
            
            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                	throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                	result.add(population,
                			   Float.parseFloat(tokens[LATITUDE_INDEX]),
                		       Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
	}

	// argument 1: file name for input data: pass this to parse
	// argument 2: number of x-dimension buckets
	// argument 3: number of y-dimension buckets
	// argument 4: -v1, -v2, -v3, -v4, or -v5
	public static void main(String[] args){
                long startPreTime = 0, startTotalTime = 0;
                if (args.length >= 6 && args[4].equals("--evaluate"))
		{
                    startTotalTime = Instant.now().getEpochSecond();
                    startPreTime = Instant.now().getEpochSecond();
                }
		VersionObject version = null;
		System.out.println(args[1]);
                CensusData c1 = parse(args[0]);

		QueryResult query = null;               
                int xGrid = Integer.parseInt(args[2]);
                int yGrid = Integer.parseInt(args[3]);  

		// Initialize the versions
                if(args[1].equals("-v1"))
                {
                    System.out.println("Running version 1");
                    version = new Version1(c1, xGrid, yGrid);
                    
                }
                if(args[1].equals("-v2"))
                {
                    System.out.println("Running version 2");
                    version = new Version2(c1, xGrid, yGrid);
                    // query = version.query(west, south, east, north);
                }
                if(args[1].equals("-v3"))
                {
                    System.out.println("Running version 3");
                    version = new Version3(c1, xGrid, yGrid);
                   
                }
                if(args[1].equals("-v4"))
                {
                    System.out.println("Running version 4");
                    version = new Version4(c1, xGrid, yGrid);
                    
                }
                if(args[1].equals("-v5"))
                {
                    System.out.println("Running on version 5");
                    version = new Version5(c1, xGrid, yGrid);
                }

		// Run an evaluation if `--evaluate` is passed as a 5th
		// command line argument
                long endPreTime = Instant.now().getEpochSecond();
		if (args.length >= 6 && args[4].equals("--evaluate"))
		{
		    int count = Integer.parseInt(args[5]);                    
		    System.out.println("Running evaluation");
                    String file = "";
                    if(args.length == 7)
                    {
                        file = args[6];
                    }
                    long totalPreTime = endPreTime - startPreTime + 1;
                    System.out.println("Total Build time: " + totalPreTime + "s");
		    evaluate(version, xGrid, yGrid, count, file);                    
                    long endTotalTime = Instant.now().getEpochSecond();                    
                    long totalRunTime = endTotalTime - startTotalTime + 1;                    
                    System.out.println("Total Run time: " + totalRunTime + "s");
		    return;
		}

                boolean running = true;
                int west = 0;
                int east = 0;
                int north = 0;
                int south = 0;               
                while(running)
                {
                    System.out.println("Please give west, south, east, north coordinates of your query rectangle:\n>>");
                    Scanner userInput = new Scanner(System.in);
                    String gridInput = userInput.nextLine();  // Read user input
                    String[] gridValues = gridInput.split(" ", 4);
                    
                    try
                    {
                        west = Integer.parseInt(gridValues[0]);
                        south = Integer.parseInt(gridValues[1]);
                        east = Integer.parseInt(gridValues[2]);
                        north = Integer.parseInt(gridValues[3]);
                    }
                    catch(Exception e)
                    {
                        running = false;
                    }

                    query = version.query(west, south, east, north); //int min_long, int max_lat, int max_long, int min_lat

                    if(running)
                    {                    
                        System.out.println("population of rectangle: " + query.population);
                        System.out.println("percent of total population: " + query.percentage + "%");
                    }
                    else
                    {
                        System.out.println("Happy end of semester and \nMeery Christmas :D");
                    }
                    
                }
	}

	private static int INITIAL_QUERY_COUNT = 10;

	public static int bound(int num, int min, int max)
	{
		num = num > 0 ? num : -num;
		return min + (num % (max - min + 1));
	}

	public static void evaluate(VersionObject version, int xGrid, int yGrid, int count, String file)
	{
		Random rng = new Random();
                int[][] queries = new int[count][4];                
                int min_x, min_y, max_x, max_y;
                
                // creates the file
                //Created the file with the querries
                //left commented because we only needed this file to be generated once
                //if a new file is needed un comment this section of code
                /*
                File Testfile = new File("QerryTests.txt");
                try {
                    Testfile.createNewFile();

                    // creates a FileWriter Object
                    FileWriter writer; 
                    writer = new FileWriter(Testfile);                   
                
                    for (long i = 0; i < 2000000000; ++i)
                    {
                            min_x = bound(rng.nextInt(), 1, xGrid);
                            min_y = bound(rng.nextInt(), 1, yGrid);
                            max_x = bound(rng.nextInt(), min_x, xGrid);
                            max_y = bound(rng.nextInt(), min_y, yGrid);
                            writer.write(String.valueOf(min_x) + " " + String.valueOf(min_y) + " " + String.valueOf(max_x) + " " + String.valueOf(max_y) + "\n");
                    }
                    // Writes the content to the file
                    writer.flush();
                    writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PopulationQuery.class.getName()).log(Level.SEVERE, null, ex);
                    }
                */
                // Run a number of initial test queries 
		for (int i = 0; i < INITIAL_QUERY_COUNT; ++i)
		{
			min_x = bound(rng.nextInt(), 1, xGrid);
			min_y = bound(rng.nextInt(), 1, yGrid);
			max_x = bound(rng.nextInt(), min_x, xGrid);
			max_y = bound(rng.nextInt(), min_y, yGrid);
			version.query(min_x, min_y, max_x, max_y);
		}
                if(file.equals(""))
                {
                    // Generate the queries randomly
                    for (int i = 0; i < count; ++i)
                    {
                            queries[i][0] = bound(rng.nextInt(), 1, xGrid);
                            queries[i][1] = bound(rng.nextInt(), 1, yGrid);
                            queries[i][2] = bound(rng.nextInt(), queries[i][0], xGrid);
                            queries[i][3] = bound(rng.nextInt(), queries[i][1], yGrid);
                    }
                }
                else
                {
                    //Generate the queries from file
                    try {
                        BufferedReader fileIn = new BufferedReader(new FileReader(file));

                        String oneLine; // skip the first line
                        int counter = 0;
                        // read each subsequent line and add relevant data to a big array
                        while ((oneLine = fileIn.readLine()) != null && counter < count) {
                            String[] currentQuerry = oneLine.split(" ");                        
                            queries[counter][0] = Integer.parseInt(currentQuerry[0]);
                            queries[counter][1] = Integer.parseInt(currentQuerry[1]);
                            queries[counter][2] = Integer.parseInt(currentQuerry[2]);
                            queries[counter][3] = Integer.parseInt(currentQuerry[3]);                        
                            counter ++;
                        }

                        fileIn.close();
                    } catch(IOException ioe) {
                        System.err.println("Error opening/reading input or output file.");
                        System.exit(1);
                    }
                }

		// Run the actual queries and the timing code
                QueryResult[] querylist = new QueryResult[count];
		long startTime = Instant.now().getEpochSecond();
		for (int i = 0; i < count; ++i)
		{
			querylist[i] = version.query(queries[i][0], queries[i][1], queries[i][2], queries[i][3]);
		}
		long endTime = Instant.now().getEpochSecond();
                
                
                //saving query results
                File Testfile = new File("QerryResults.txt");
                try {
                    Testfile.createNewFile();

                    // creates a FileWriter Object
                    FileWriter writer; 
                    writer = new FileWriter(Testfile);                   
                
                    for (int i = 0; i < count; ++i)
                    {                            
                            writer.write(String.valueOf(queries[i][0]) + " " + String.valueOf(queries[i][1]) + " " + String.valueOf(queries[i][2]) + " " + String.valueOf(queries[i][3]) +
                            " = # " + String.valueOf(querylist[i].population) + " % " + String.valueOf(querylist[i].percentage) +"\n");
                    }
                    // Writes the content to the file
                    writer.flush();
                    writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PopulationQuery.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
		long totalTime = endTime - startTime + 1;
		System.out.println("Total querry time: " + totalTime + "s");
	}
}
