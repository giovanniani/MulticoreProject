package multicoreproject.version5;
import multicoreproject.version4.*;
import multicoreproject.CensusGroup;
import multicoreproject.version2.Boundary;

public class Grid {
	public GridItem[][] data;
	private Boundary _bounds;
	private float _cell_x;
	private float _cell_y;
	private int _grid_x;
	private int _grid_y;

	public Grid(Boundary bounds, int grid_x, int grid_y) {
		data = new GridItem[grid_x][grid_y];
		// Initialize the grid
		for (int x = 0; x < grid_x; ++x) {
			for (int y = 0; y < grid_y; ++y) {
				data[x][y] = new GridItem();
				data[x][y].updatePopulation(0);
			}
		}
		_bounds = bounds;
		_cell_x = (bounds.maxLatitude - bounds.minLatitude) / ((float)grid_x);
		_cell_y = (bounds.maxLongitude - bounds.minLongitude) / ((float)grid_y);
		_grid_x = grid_x;
		_grid_y = grid_y;
	}

	// Make an empty copy of this grid with all the same parameters
	public Grid emptyCopy() {
		return new Grid(_bounds, _grid_x, _grid_y);
	}

	public void update(CensusGroup group) {
		// Calculate x and y
		int x = (int)((group.latitude - _bounds.minLatitude) / _cell_x);
		int y = (int)((group.longitude - _bounds.minLongitude) / _cell_y);
		x = (x >= _grid_x) ? (_grid_x - 1) : x;
		y = (y >= _grid_y) ? (_grid_y - 1) : y;
		data[x][y].updatePopulation(group.population);
	}

	// Combine two grids together
	public void combine(Grid other) {
		for (int x = 0; x < _grid_x; ++x) {
			for (int y = 0; y < _grid_y; ++y) {
				data[x][y].updatePopulation(other.data[x][y].getValue());
			}
		}
	}
}
