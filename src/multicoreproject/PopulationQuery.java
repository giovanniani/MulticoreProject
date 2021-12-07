package multicoreproject;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner; 
import java.util.Random;
import java.time.Instant;


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
	public static void main(String[] args) {
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
                    // query = version.query(1, 1, 100, 500);
                }
                if(args[1].equals("-v5"))
                {
                    System.out.println("Running on version 5");
                }

		// Run an evaluation if `--evaluate` is passed as a 5th
		// command line argument
		if (args.length >= 6 && args[4].equals("--evaluate"))
		{
		    int count = Integer.parseInt(args[5]);
		    System.out.println("Running evaluation");
		    evaluate(version, xGrid, yGrid, count);
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

	public static void evaluate(VersionObject version, int xGrid, int yGrid, int count)
	{
		// Run a number of initial test queries
		Random rng = new Random();
		for (int i = 0; i < INITIAL_QUERY_COUNT; ++i)
		{
			int min_x = bound(rng.nextInt(), 1, xGrid);
			int min_y = bound(rng.nextInt(), 1, yGrid);
			int max_x = bound(rng.nextInt(), min_x, xGrid);
			int max_y = bound(rng.nextInt(), min_y, yGrid);
			version.query(min_x, min_y, max_x, max_y);
		}

		// Generate the queries
		int[][] queries = new int[count][4];
		for (int i = 0; i < count; ++i)
		{
			queries[i][0] = bound(rng.nextInt(), 1, xGrid);
			queries[i][1] = bound(rng.nextInt(), 1, yGrid);
			queries[i][2] = bound(rng.nextInt(), queries[i][0], xGrid);
			queries[i][3] = bound(rng.nextInt(), queries[i][1], yGrid);
		}

		// Run the actual queries and the timing code
		long startTime = Instant.now().getEpochSecond();
		for (int i = 0; i < count; ++i)
		{
			version.query(queries[i][0], queries[i][1], queries[i][2], queries[i][3]);
		}
		long endTime = Instant.now().getEpochSecond();

		long totalTime = endTime - startTime + 1;
		System.out.println("Total time: " + totalTime + "s");
	}
}
