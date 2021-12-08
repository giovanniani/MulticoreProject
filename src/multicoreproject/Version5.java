package multicoreproject;

import multicoreproject.version5.GridItem;
import multicoreproject.version2.BoundaryFinder;
import multicoreproject.version2.Boundary;
import multicoreproject.version5.GridBuilder;
import multicoreproject.version5.Grid;

public class Version5 extends VersionObject {
    private GridItem[][] _grid;
    private int _total_pop;

    public Version5(CensusData data, int grid_x, int grid_y) {


        Runtime runtime = Runtime.getRuntime();
         
        // get the number of processors available to the Java virtual machine
        int numberOfProcessors = runtime.availableProcessors();
        
        // Calculate the bounds
        Boundary bounds = BoundaryFinder.findBoundaries(data);
        // Build the grid
        
        //update in order to make thread friendly
        _grid = GridBuilder.buildGrid(data, grid_x, grid_y, bounds);

        // Step 2: Update the grid so that each cell holds the total
        // population for all cells that are farther south or farther
        // west
        _total_pop = 0;
        for (int x = 0; x < grid_x; ++x) {
            for (int y = 0; y < grid_y; ++y) {
                // Also calculate the total population here
                _total_pop += _grid[x][y].getValue();
                int tmp0 = 0;
                if (y > 0) {
                        tmp0 = _grid[x][y - 1].getValue();
                }
                int tmp1 = 0;
                if (x > 0) {
                        tmp1 = _grid[x - 1][y].getValue();
                }
                int tmp2 = 0;
                if (x > 0 && y > 0) {
                        tmp2 = _grid[x - 1][y - 1].getValue();
                }
                _grid[x][y].updatePopulation(_grid[x][y].getValue() + tmp0 + tmp1 - tmp2);
            }
        }
    }

    QueryResult query(int min_x, int min_y, int max_x, int max_y) {
            int pop = _grid[max_x - 1][max_y - 1].getValue();
            if (min_x > 1)
                    pop -= _grid[min_x - 2][max_y - 1].getValue();
            if (min_y > 1)
                    pop -= _grid[max_x - 1][min_y - 2].getValue();
            if (min_x > 1 && min_y > 1)
                    pop += _grid[min_x - 2][min_y - 2].getValue();
            return new QueryResult(pop, ((float)100.0) * ((float)pop) / _total_pop);
    }
}
