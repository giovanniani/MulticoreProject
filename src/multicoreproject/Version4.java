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
		// _grid = new Grid(data, grid_x, grid_y);
		_grid = GridBuilder.buildGrid(data, grid_x, grid_y, bounds);

/*
		_grid = new int[grid_x][grid_y];
		// Initialize the grid
		for (int i = 0; i < grid_x; ++i)
			for (int j = 0; j < grid_y; ++j)
				_grid[i][j] = 0;
		// Step 1: Total the grid (TODO: Do this in parallel)
		float cell_x = (bounds.maxLatitude - bounds.minLatitude) / grid_x;
		float cell_y = (bounds.maxLongitude - bounds.minLongitude) / grid_y;
		_total_pop = 0;
		for (int i = 0; i < data.data_size; ++i) {
			for (int x = 0; x < grid_x; ++x) {
				float min_lat = bounds.minLatitude + x * cell_x;
				float max_lat = min_lat + cell_x;
				for (int y = 0; y < grid_y; ++y) {
					float min_long = bounds.minLongitude + y * cell_y;
					float max_long = min_long + cell_y;
					if (data.data[i].latitude >= min_lat
						&& data.data[i].latitude < max_lat
						&& data.data[i].longitude >= min_long
						&& data.data[i].longitude < max_long) {
						_grid[x][y] += data.data[i].population;
					}
				}
			}
			_total_pop += data.data[i].population;
		}
*/
		// Step 2: Update the grid so that each cell holds the total
		// population for all cells that are farther south or farther
		// west
		// TODO: This is not exactly correct
		for (int x = 0; x < grid_x; ++x) {
			for (int y = 0; y < grid_y; ++y) {
				// _grid[x][y] = _grid[x][y] + _grid[x - 1][y] + _grid[x][y - 1];
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
		// TODO: Preprocessing
		// Create a grid of x*y size
		// TODO
		// Traverse over the input data
		// Step 1 (parallel)
		// TODO
		// Step 2 (sequential)
		// TODO
	}

	QueryResult query(int min_x, int min_y, int max_x, int max_y) {
		// TODO: This query isn't quite right
		int pop = _grid[max_x - 1][max_y - 1] - _grid[min_x - 1][min_y - 1];
		return new QueryResult(pop, ((float)100.0) * ((float)pop) / _total_pop);
	}
}
