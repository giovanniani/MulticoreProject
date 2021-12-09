package multicoreproject;

import multicoreproject.version2.BoundaryFinder;
import multicoreproject.version2.Boundary;
import multicoreproject.version4.GridBuilder;
import multicoreproject.version4.Grid;

public class Version4 extends VersionObject {
	private int[][] _grid;
	private int _total_pop;

	public Version4(CensusData data, int grid_x, int grid_y) {
		// Calculate the bounds
		Boundary bounds = BoundaryFinder.findBoundaries(data);
		// Build the grid
		_grid = GridBuilder.buildGrid(data, grid_x, grid_y, bounds);

		// Step 2: Update the grid so that each cell holds the total
		// population for all cells that are farther south or farther
		// west
		_total_pop = 0;
		for (int x = 0; x < grid_x; ++x) {
			for (int y = 0; y < grid_y; ++y) {
				// Also calculate the total population here
				_total_pop += _grid[x][y];
				int tmp0 = 0;
				if (y > 0) {
					tmp0 = _grid[x][y - 1];
				}
				int tmp1 = 0;
				if (x > 0) {
					tmp1 = _grid[x - 1][y];
				}
				int tmp2 = 0;
				if (x > 0 && y > 0) {
					tmp2 = _grid[x - 1][y - 1];
				}
				_grid[x][y] = _grid[x][y] + tmp0 + tmp1 - tmp2;
			}
		}
	}

	QueryResult query(int min_x, int min_y, int max_x, int max_y) {
		int pop = _grid[max_x - 1][max_y - 1];
		if (min_x > 1)
			pop -= _grid[min_x - 2][max_y - 1];
		if (min_y > 1)
			pop -= _grid[max_x - 1][min_y - 2];
		if (min_x > 1 && min_y > 1)
			pop += _grid[min_x - 2][min_y - 2];
		return new QueryResult(pop, ((float)100.0) * ((float)pop) / _total_pop);
	}
        
        public String getVersion()
        {
            return "Version4";
        }
}
