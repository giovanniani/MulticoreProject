package multicoreproject.version4;
import multicoreproject.CensusGroup;
import multicoreproject.version2.Boundary;

public class Grid {
	public int[][] data;
	private Boundary _bounds;
	private int _cell_x;
	private int _cell_y;
	private int _grid_x;
	private int _grid_y;

	public Grid(Boundary bounds, int grid_x, int grid_y) {
		data = new int[grid_x][grid_y];
		for (int x = 0; x < grid_x; ++x) {
			for (int y = 0; y < grid_y; ++y) {
				data[x][y] = 0;
			}
		}
		_bounds = bounds;
		_cell_x = (int)((bounds.maxLatitude - bounds.minLatitude) / ((float)grid_x));
		_cell_y = (int)((bounds.maxLongitude - bounds.minLongitude) / ((float)grid_y));
		System.out.print("cell_x: " + _cell_x + ", cell_y: " + _cell_y);
		_grid_x = grid_x;
		_grid_y = grid_y;
	}

	// Make an empty copy of this grid with all the same parameters
	public Grid emptyCopy() {
		return new Grid(_bounds, _grid_x, _grid_y);
	}

	public void update(CensusGroup group) {
		// Calculate x and y
		int x = (int)((group.latitude - _bounds.minLatitude) / ((float)_cell_x));
		int y = (int)((group.longitude - _bounds.minLongitude) / ((float)_cell_y));
		data[x][y] += group.population;
	}

	// Combine two grids together
	public void combine(Grid other) {
		for (int x = 0; x < _grid_x; ++x) {
			for (int y = 0; y < _grid_y; ++y) {
				data[x][y] += other.data[x][y];
			}
		}
	}
}
