package multicoreproject;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner; 


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
                System.out.println("Please give west, south, east, north coordinates of your query rectangle:\n>>");
                Scanner userInput = new Scanner(System.in);
                String gridInput = userInput.nextLine();  // Read user input
                String[] gridValues = gridInput.split(" ", 4);
                System.out.println(gridValues); //west, south, east, north 
                int west = Integer.parseInt(gridValues[0]);
                int south = Integer.parseInt(gridValues[1]);
                int east = Integer.parseInt(gridValues[2]);
                int north = Integer.parseInt(gridValues[3]);
                int xGrid = Integer.parseInt(args[2]);
                int yGrid = Integer.parseInt(args[3]);                

                if(args[1].equals("-v1"))
                {
                    System.out.println("Running version 1");
                    version = new Version1(c1, xGrid, yGrid);
                    query = version.query(west, south, east, north); //int min_long, int max_lat, int max_long, int min_lat
                }
                if(args[1].equals("-v2"))
                {
                    System.out.println("Running version 2");
                    version = new Version2(c1, xGrid, yGrid);
		    query = version.query(west, south, east, north);
                }
                if(args[1].equals("-v3"))
                {
                    System.out.println("Running version 3");
                }
                if(args[1].equals("-v4"))
                {
                    System.out.println("Running version 4");
		    version = new Version4(c1, grid_x, grid_y);
		    query = version.query(1, 1, 100, 500);
                }
                if(args[1].equals("-v5"))
                {
                    System.out.println("Running on version 5");
                }
                System.out.println("population of rectangle: " + query.population);
                System.out.println("percent of total population: " + query.percentage + "%");

		// TODO: Eventually write query loop
	}
}
