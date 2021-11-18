package multicoreproject;
import multicoreproject.version2.*;

public class Version2 extends VersionObject {
	private CensusData _data;
	private int _grid_x;
	private int _grid_y;

	public Version2(CensusData data, int grid_x, int grid_y) {
		_data = data;
		_grid_x = grid_x;
		_grid_y = grid_y;
	}

	public QueryResult query(int min_x, int max_x, int min_y, int max_y) {
		Boundary bounds = BoundaryFinder.findBoundaries(_data);
		QueryResult result = Query.query(_data, bounds, _grid_x, _grid_y, min_x, max_x, min_y, max_y);
		return result;
	}
}
